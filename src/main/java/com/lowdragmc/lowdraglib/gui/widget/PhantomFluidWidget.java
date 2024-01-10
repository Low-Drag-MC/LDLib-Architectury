package com.lowdragmc.lowdraglib.gui.widget;

import com.google.common.collect.Lists;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import dev.emi.emi.api.stack.FluidEmiStack;
import net.minecraft.core.NonNullList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@LDLRegister(name = "phantom_fluid_slot", group = "widget.container")
public class PhantomFluidWidget extends TankWidget implements IGhostIngredientTarget, IConfigurableWidget {

    private Consumer<FluidStack> fluidStackUpdater;

    public PhantomFluidWidget() {
        super();
        this.allowClickFilled = false;
        this.allowClickDrained = false;
    }

    public PhantomFluidWidget(IFluidHandler fluidTank, int x, int y) {
        super(fluidTank, x, y, false, false);
    }

    public PhantomFluidWidget(@Nullable IFluidHandler fluidTank, int x, int y, int width, int height) {
        super(fluidTank, x, y, width, height, false, false);
    }

    public PhantomFluidWidget setIFluidStackUpdater(Consumer<FluidStack> fluidStackUpdater) {
        this.fluidStackUpdater = fluidStackUpdater;
        return this;
    }

    @ConfigSetter(field = "allowClickFilled")
    public PhantomFluidWidget setAllowClickFilled(boolean v) {
        // you cant modify it
        return this;
    }

    @ConfigSetter(field = "allowClickDrained")
    public PhantomFluidWidget setAllowClickDrained(boolean v) {
        // you cant modify it
        return this;
    }

    public static FluidStack drainFrom(Object ingredient) {
         if (ingredient instanceof Ingredient ingred) {
            var items = ingred.getItems();
            if (items.length > 0) {
                ingredient = items[0];
            }
        }
        if (ingredient instanceof ItemStack itemStack) {
            var handler = FluidTransferHelper.getFluidTransfer(new ItemStackHandler(NonNullList.of(ItemStack.EMPTY, itemStack)), 0);
            if (handler != null) {
                return handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
            }
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Target> getPhantomTargets(Object ingredient) {
        if (LDLib.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
            ingredient = new FluidStack(fluidStack.getFluid(), fluidTank.getTankCapacity(0), fluidStack.getTag());
        }
        if (LDLib.isEmiLoaded() && ingredient instanceof FluidEmiStack fluidEmiStack) {
            var fluid = fluidEmiStack.getKeyOfType(Fluid.class);
            ingredient = fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, fluidTank.getTankCapacity(0), fluidEmiStack.getNbt());
        }
        if (!(ingredient instanceof FluidStack) && drainFrom(ingredient) == null) {
            return Collections.emptyList();
        }

        Rect2i rectangle = toRectangleBox();
        return Lists.newArrayList(new Target() {
            @Nonnull
            @Override
            public Rect2i getArea() {
                return rectangle;
            }

            @Override
            public void accept(@Nonnull Object ingredient) {
                if (fluidTank == null) return;
                FluidStack ingredientStack;
                if (LDLib.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
                    ingredient = new FluidStack(fluidStack.getFluid(), fluidTank.getTankCapacity(0), fluidStack.getTag());
                }
                if (LDLib.isEmiLoaded() && ingredient instanceof FluidEmiStack fluidEmiStack) {
                    var fluid = fluidEmiStack.getKeyOfType(Fluid.class);
                    ingredient = fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, fluidTank.getTankCapacity(0), fluidEmiStack.getNbt());
                }
                if (ingredient instanceof FluidStack)
                    ingredientStack = (FluidStack) ingredient;
                else
                    ingredientStack = drainFrom(ingredient);

                if (ingredientStack != null) {
                    CompoundTag tagCompound = ingredientStack.writeToNBT(new CompoundTag());
                    writeClientAction(2, buffer -> buffer.writeNbt(tagCompound));
                }

                if (isClientSideWidget) {
                    fluidTank.drain(fluidTank.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
                    if (ingredientStack != null) {
                        fluidTank.fill(ingredientStack.copy(), IFluidHandler.FluidAction.EXECUTE);
                    }
                    if (fluidStackUpdater != null) {
                        fluidStackUpdater.accept(ingredientStack);
                    }
                }
            }
        });
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            handlePhantomClick();
        } else if (id == 2) {
            FluidStack fluidStack;
            fluidStack = FluidStack.loadFluidStackFromNBT(buffer.readNbt());
            if (fluidTank == null) return;
            fluidTank.drain(fluidTank.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
            if (fluidStack != null) {
                fluidTank.fill(fluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);
            }
            if (fluidStackUpdater != null) {
                fluidStackUpdater.accept(fluidStack);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            if (isClientSideWidget) {
                handlePhantomClick();
            } else {
                writeClientAction(1, buffer -> { });
            }
            return true;
        }
        return false;
    }

    private void handlePhantomClick() {
        if (fluidTank == null) return;
        ItemStack itemStack = gui.getModularUIContainer().getCarried().copy();
        if (!itemStack.isEmpty()) {
            itemStack.setCount(1);
            var handler = FluidTransferHelper.getFluidTransfer(gui.entityPlayer, gui.getModularUIContainer());
            if (handler != null) {
                FluidStack resultFluid = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                fluidTank.drain(fluidTank.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
                fluidTank.fill(resultFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                if (fluidStackUpdater != null) {
                    fluidStackUpdater.accept(resultFluid);
                }
            }
        } else {
            fluidTank.drain(fluidTank.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
            if (fluidStackUpdater != null) {
                fluidStackUpdater.accept(null);
            }
        }
    }

}
