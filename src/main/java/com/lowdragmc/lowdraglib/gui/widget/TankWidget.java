package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.side.fluid.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.FluidEmiStack;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@LDLRegister(name = "fluid_slot", group = "widget.container")
@Accessors(chain = true)
public class TankWidget extends Widget implements IRecipeIngredientSlot, IConfigurableWidget {

    @Nullable
    @Getter
    protected IFluidHandler fluidTank;
    @Configurable
    @Setter
    protected boolean showAmount;
    @Configurable
    @Setter
    protected boolean allowClickFilled;
    @Configurable
    @Setter
    protected boolean allowClickDrained;
    @Configurable
    @Setter
    public boolean drawHoverOverlay = true;
    @Configurable
    @Setter
    protected boolean drawHoverTips;
    @Configurable
    @Setter
    protected ProgressTexture.FillDirection fillDirection = ProgressTexture.FillDirection.ALWAYS_FULL;
    @Configurable
    @Setter
    protected IGuiTexture overlay;
    @Setter
    protected BiConsumer<TankWidget, List<Component>> onAddedTooltips;
    @Setter @Getter
    protected IngredientIO ingredientIO = IngredientIO.RENDER_ONLY;
    @Setter @Getter
    protected float XEIChance = 1f;
    protected FluidStack lastFluidInTank;
    protected long lastTankCapacity;
    @Setter
    protected Runnable changeListener;
    @NotNull
    protected List<Consumer<List<Component>>> tooltipCallback = new ArrayList<>();

    public TankWidget() {
        this(null, 0, 0, 18, 18, true, true);
    }

    @Override
    public void initTemplate() {
        setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"));
        setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
    }

    public TankWidget(IFluidHandler fluidTank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this(fluidTank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public TankWidget(@Nullable IFluidHandler fluidTank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(width, height));
        this.fluidTank = fluidTank;
        this.showAmount = true;
        this.allowClickFilled = allowClickContainerFilling;
        this.allowClickDrained = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget setFluidTank(IFluidHandler fluidTank) {
        this.fluidTank = fluidTank;
        if (isClientSideWidget) {
            setClientSideWidget();
        }
        return this;
    }

    @Override
    public TankWidget setClientSideWidget() {
        super.setClientSideWidget();
        if (fluidTank != null) {
            fluidTank.getFluidInTank(0);
            this.lastFluidInTank = fluidTank.getFluidInTank(0).copy();
        } else {
            this.lastFluidInTank = null;
        }
        this.lastTankCapacity = fluidTank != null ? fluidTank.getTankCapacity(0) : 0;
        return this;
    }

    public TankWidget setBackground(IGuiTexture background) {
        super.setBackground(background);
        return this;
    }

    @Nullable
    @Override
    public Object getXEIIngredientOverMouse(double mouseX, double mouseY) {
        if (self().isMouseOverElement(mouseX, mouseY)) {
            if (lastFluidInTank == null || lastFluidInTank.isEmpty()) return null;
            if (LDLib.isJeiLoaded()) {
                return null;
                //return getPlatformFluidTypeForJEI(lastFluidInTank, getPosition(), getSize());
            }
            if (LDLib.isReiLoaded()) {
                return EntryStacks.of(dev.architectury.fluid.FluidStack.create(lastFluidInTank.getFluid(), lastFluidInTank.getAmount(), lastFluidInTank.getTag()));
            }
            if (LDLib.isEmiLoaded()) {
                return new FluidEmiStack(lastFluidInTank.getFluid(), lastFluidInTank.getTag(), lastFluidInTank.getAmount()).setChance(XEIChance);
            }
        }
        return null;
    }

    @Override
    public List<Object> getXEIIngredients() {
        if (lastFluidInTank == null || lastFluidInTank.isEmpty()) return Collections.emptyList();
        if (LDLib.isJeiLoaded()) {
            return List.of(/*getPlatformFluidTypeForJEI(lastFluidInTank, getPosition(), getSize())*/);
        }
        if (LDLib.isReiLoaded()) {
            return List.of(EntryStacks.of(dev.architectury.fluid.FluidStack.create(lastFluidInTank.getFluid(), lastFluidInTank.getAmount(), lastFluidInTank.getTag())));
        }
        if (LDLib.isEmiLoaded()) {
            return List.of(new FluidEmiStack(lastFluidInTank.getFluid(), lastFluidInTank.getTag(), lastFluidInTank.getAmount()).setChance(XEIChance));
        }
        return List.of(FluidHelper.toRealFluidStack(lastFluidInTank));
    }

    /*
    public static Object getPlatformFluidTypeForJEI(FluidStack fluidStack, Position pos, Size size) {
        return new ClickableIngredient<>(TypedIngredient.createUnvalidated(ForgeTypes.FLUID_STACK, new net.neoforged.neoforge.fluids.FluidStack(fluidStack.getFluid(), (int) fluidStack.getAmount(), fluidStack.getTag())),
                new ImmutableRect2i(pos.x, pos.y, size.width, size.height));
    }
    */

    private List<Component> getToolTips(List<Component> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        for (Consumer<List<Component>> callback : this.tooltipCallback) {
            callback.accept(list);
        }

        return list;
    }

    @Override
    public void addTooltipCallback(Consumer<List<Component>> callback) {
        this.tooltipCallback.add(callback);
    }

    @Override
    public void clearTooltipCallback() {
        this.tooltipCallback.clear();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        if (isClientSideWidget && fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluidInTank(0);
            if (fluidTank.getTankCapacity(0) != lastTankCapacity) {
                this.lastTankCapacity = fluidTank.getTankCapacity(0);
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
            } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
                this.lastFluidInTank.setAmount(fluidStack.getAmount());
            }
        }
        Position pos = getPosition();
        Size size = getSize();
        if (lastFluidInTank != null) {
            RenderSystem.disableBlend();
            if (!lastFluidInTank.isEmpty()) {
                double progress = lastFluidInTank.getAmount() * 1.0 / Math.max(Math.max(lastFluidInTank.getAmount(), lastTankCapacity), 1);
                float drawnU = (float) fillDirection.getDrawnU(progress);
                float drawnV = (float) fillDirection.getDrawnV(progress);
                float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
                float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
                int width = size.width - 2;
                int height = size.height - 2;
                int x = pos.x + 1;
                int y = pos.y + 1;
                DrawerHelper.drawFluidForGui(graphics, lastFluidInTank, lastFluidInTank.getAmount(), (int) (x + drawnU * width), (int) (y + drawnV * height), ((int) (width * drawnWidth)), ((int) (height * drawnHeight)));
            }

            if (showAmount && !lastFluidInTank.isEmpty()) {
                graphics.pose().pushPose();
                graphics.pose().scale(0.5F, 0.5F, 1);
                String s = TextFormattingUtil.formatLongToCompactStringBuckets(lastFluidInTank.getAmount(), 3) + "B";
                Font fontRenderer = Minecraft.getInstance().font;
                graphics.drawString(fontRenderer, s, (int) ((pos.x + (size.width / 3f)) * 2 - fontRenderer.width(s) + 21), (int) ((pos.y + (size.height / 3f) + 6) * 2), 0xFFFFFF, true);
                graphics.pose().popPose();
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        if (overlay != null) {
            overlay.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
        if (drawHoverOverlay && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(graphics, getPosition().x + 1, getPosition().y + 1, getSize().width - 2, getSize().height - 2, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)

    public void drawInForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            List<Component> tooltips = new ArrayList<>();
            if (lastFluidInTank != null && !lastFluidInTank.isEmpty()) {
                tooltips.add(FluidHelper.getDisplayName(lastFluidInTank));
                tooltips.add(Component.translatable("ldlib.fluid.amount", lastFluidInTank.getAmount(), lastTankCapacity).append(" " + FluidHelper.getUnit()));
                if (!Platform.isForge()) {
                    tooltips.add(Component.literal("§6mB:§r %d/%d".formatted(lastFluidInTank.getAmount() * 1000 / FluidHelper.getBucket(), lastTankCapacity * 1000 / FluidHelper.getBucket())).append(" " + "mB"));
                }
                tooltips.add(Component.translatable("ldlib.fluid.temperature", FluidHelper.getTemperature(lastFluidInTank)));
                tooltips.add(Component.translatable(FluidHelper.isLighterThanAir(lastFluidInTank) ? "ldlib.fluid.state_gas" : "ldlib.fluid.state_liquid"));
            } else {
                tooltips.add(Component.translatable("ldlib.fluid.empty"));
                tooltips.add(Component.translatable("ldlib.fluid.amount", 0, lastTankCapacity).append(" " + FluidHelper.getUnit()));
                if (!Platform.isForge()) {
                    tooltips.add(Component.literal("§6mB:§r %d/%d".formatted(0, lastTankCapacity * 1000 / FluidHelper.getBucket())).append(" " + "mB"));
                }
            }
            if (gui != null) {
                tooltips.addAll(tooltipTexts);
                gui.getModularUIGui().setHoverTooltip(getToolTips(tooltips), ItemStack.EMPTY, null, null);
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        } else {
            super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void detectAndSendChanges() {
        if (fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluidInTank(0);
            if (fluidTank.getTankCapacity(0) != lastTankCapacity) {
                this.lastTankCapacity = fluidTank.getTankCapacity(0);
                writeUpdateInfo(0, buffer -> buffer.writeVarLong(lastTankCapacity));
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
                CompoundTag fluidStackTag = fluidStack.writeToNBT(new CompoundTag());
                writeUpdateInfo(2, buffer -> buffer.writeNbt(fluidStackTag));
            } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
                this.lastFluidInTank.setAmount(fluidStack.getAmount());
                writeUpdateInfo(3, buffer -> buffer.writeVarLong(lastFluidInTank.getAmount()));
            } else {
                super.detectAndSendChanges();
                return;
            }
            if (changeListener != null) {
                changeListener.run();
            }
        }
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(fluidTank != null);
        if (fluidTank != null) {
            this.lastTankCapacity = fluidTank.getTankCapacity(0);
            buffer.writeVarLong(lastTankCapacity);
            FluidStack fluidStack = fluidTank.getFluidInTank(0);
            this.lastFluidInTank = fluidStack.copy();
            buffer.writeNbt(fluidStack.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            this.lastTankCapacity = buffer.readVarLong();
            readUpdateInfo(2, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarLong();
        } else if (id == 1) {
            this.lastFluidInTank = null;
        } else if (id == 2) {
            this.lastFluidInTank = FluidStack.loadFluidStackFromNBT(buffer.readNbt());
        } else if (id == 3 && lastFluidInTank != null) {
            this.lastFluidInTank.setAmount(buffer.readVarInt());
        } else if (id == 4) {
            ItemStack currentStack = gui.getModularUIContainer().getCarried();
            int newStackSize = buffer.readVarInt();
            currentStack.setCount(newStackSize);
            gui.getModularUIContainer().setCarried(currentStack);
        } else {
            super.readUpdateInfo(id, buffer);
            return;
        }
        if (changeListener != null) {
            changeListener.run();
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            boolean isShiftKeyDown = buffer.readBoolean();
            int clickResult = tryClickContainer(isShiftKeyDown);
            if (clickResult >= 0) {
                writeUpdateInfo(4, buf -> buf.writeVarInt(clickResult));
            }
        }
    }

    private int tryClickContainer(boolean isShiftKeyDown) {
        if (fluidTank == null) return -1;
        Player player = gui.entityPlayer;
        ItemStack currentStack = gui.getModularUIContainer().getCarried();
        var handler = FluidTransferHelper.getFluidTransfer(gui.entityPlayer, gui.getModularUIContainer());
        if (handler == null) return -1;
        int maxAttempts = isShiftKeyDown ? currentStack.getCount() : 1;
        if (allowClickFilled && fluidTank.getFluidInTank(0).getAmount() > 0) {
            boolean performedFill = false;
            FluidStack initialFluid = fluidTank.getFluidInTank(0);
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidTransferHelper.tryFillContainer(currentStack, fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                ItemStack remainingStack = FluidTransferHelper.tryFillContainer(currentStack, fluidTank, Integer.MAX_VALUE, null, true).getResult();
                currentStack.shrink(1);
                performedFill = true;
                if (!remainingStack.isEmpty() && !player.addItem(remainingStack)) {
                    Block.popResource(player.level(), player.getOnPos(), remainingStack);
                    break;
                }
            }
            if (performedFill) {
                SoundEvent soundevent = FluidHelper.getFillSound(initialFluid);
                if (soundevent != null) {
                    player.level().playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                gui.getModularUIContainer().setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        if (allowClickDrained) {
            boolean performedEmptying = false;
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidTransferHelper.tryEmptyContainer(currentStack, fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                ItemStack remainingStack = FluidTransferHelper.tryEmptyContainer(currentStack, fluidTank, Integer.MAX_VALUE, null, true).getResult();
                currentStack.shrink(1);
                performedEmptying = true;
                if (!remainingStack.isEmpty() && !player.getInventory().add(remainingStack)) {
                    Block.popResource(player.level(), player.getOnPos(), remainingStack);
                    break;
                }
            }
            var filledFluid = fluidTank.getFluidInTank(0);
            if (performedEmptying) {
                SoundEvent soundevent = FluidHelper.getEmptySound(filledFluid);
                if (soundevent != null) {
                    player.level().playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                gui.getModularUIContainer().setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        return -1;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((allowClickDrained || allowClickFilled) && isMouseOverElement(mouseX, mouseY)) {
            if (button == 0) {
                if (FluidTransferHelper.getFluidTransfer(gui.entityPlayer, gui.getModularUIContainer()) != null) {
                    boolean isShiftKeyDown = isShiftDown();
                    writeClientAction(1, writer -> writer.writeBoolean(isShiftKeyDown));
                    playButtonClickSound();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        var handler = new FluidTank(5000);
        handler.fill(new FluidStack(Fluids.WATER, 3000), IFluidHandler.FluidAction.EXECUTE);
        father.addConfigurators(new WrapperConfigurator("ldlib.gui.editor.group.preview", new TankWidget() {
            @Override
            public void updateScreen() {
                super.updateScreen();
                setHoverTooltips(TankWidget.this.tooltipTexts);
                this.backgroundTexture = TankWidget.this.backgroundTexture;
                this.hoverTexture = TankWidget.this.hoverTexture;
                this.showAmount = TankWidget.this.showAmount;
                this.drawHoverTips = TankWidget.this.drawHoverTips;
                this.fillDirection = TankWidget.this.fillDirection;
                this.overlay = TankWidget.this.overlay;
            }
        }.setAllowClickDrained(false).setAllowClickFilled(false).setFluidTank(handler)));

        IConfigurableWidget.super.buildConfigurator(father);
    }
}
