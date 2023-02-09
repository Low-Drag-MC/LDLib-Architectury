package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class FluidStackSelectorWidget extends WidgetGroup {
    private Consumer<FluidStack> onFluidStackUpdate;
    private final FluidTank handler;
    private final TextFieldWidget fluidField;
    private FluidStack fluid = FluidStack.EMPTY;

    public FluidStackSelectorWidget(int x, int y, int width) {
        super(x, y, width, 20);
        setClientSideWidget();
        fluidField = (TextFieldWidget) new TextFieldWidget(22, 0, width - 46, 20, null, s -> {
            if (s != null && !s.isEmpty()) {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(s));
                if (fluid == null) {
                    fluid = FluidStack.EMPTY.getFluid();
                }
                if (!this.fluid.isFluidEqual(new FluidStack(fluid, 1000))) {
                    this.fluid = new FluidStack(fluid, 1000);
                    onUpdate();
                }
            }
        }).setResourceLocationOnly().setHoverTooltips("ldlib.gui.tips.fluid_selector");

        addWidget(new PhantomFluidWidget(handler = new FluidTank(1000),1, 1)
                .setFluidStackUpdater(fluidStack -> {
                    setFluidStack(fluidStack);
                    onUpdate();
                }).setBackground(new ColorBorderTexture(1, -1)));
        addWidget(fluidField);

        addWidget(new ButtonWidget(width - 21, 0, 20, 20, null, cd -> {
            if (fluid.isEmpty()) return;
            TextFieldWidget nbtField;
            new DialogWidget(getGui().mainGroup, isClientSideWidget)
                    .setOnClosed(this::onUpdate)
                    .addWidget(nbtField = new TextFieldWidget(10, 10, getGui().mainGroup.getSize().width - 50, 20, null, s -> {
                        try {
                            fluid.setTag(TagParser.parseTag(s));
                            onUpdate();
                        } catch (CommandSyntaxException ignored) {

                        }
                    }));
            if (fluid.hasTag()) {
                nbtField.setCurrentString(fluid.getTag().toString());
            }
        })
                .setButtonTexture(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("NBT", -1).setDropShadow(true))
                .setHoverBorderTexture(1, -1).setHoverTooltips("ldlib.gui.tips.fluid_tag"));
    }

    public FluidStack getFluidStack() {
        return fluid;
    }

    public FluidStackSelectorWidget setFluidStack(FluidStack fluidStack) {
        fluid = Objects.requireNonNullElse(fluidStack, FluidStack.EMPTY).copy();
        if (fluid != FluidStack.EMPTY) {
            fluid.setAmount(1000);
        }
        handler.setFluid(fluid);
        fluidField.setCurrentString(fluid.getFluid().getRegistryName().toString());
        return this;
    }

    public FluidStackSelectorWidget setOnFluidStackUpdate(Consumer<FluidStack> onFluidStackUpdate) {
        this.onFluidStackUpdate = onFluidStackUpdate;
        return this;
    }

    private void onUpdate() {
        handler.setFluid(fluid);
        if (onFluidStackUpdate != null) {
            onFluidStackUpdate.accept(fluid);
        }
    }
}
