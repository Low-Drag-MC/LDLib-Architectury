package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
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
import com.lowdragmc.lowdraglib.utils.FluidUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RegisterUI(name = "fluid_slot", group = "widget.container")
@Accessors(chain = true)
public class TankWidget extends Widget implements IRecipeIngredientSlot, IConfigurableWidget {

    @Nullable
    @Getter
    protected IFluidTank fluidTank;
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
    @Setter
    protected IngredientIO ingredientIO = IngredientIO.RENDER_ONLY;

    protected FluidStack lastFluidInTank;
    protected int lastTankCapacity;
    @Setter
    protected Runnable changeListener;
    @NotNull
    protected List<Consumer<List<Component>>> tooltipCallback = new ArrayList<>();

    public TankWidget() {
        this(null, 0, 0, 18, 18, true, true);
        setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"));
        setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
    }

    public TankWidget(IFluidTank fluidTank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this(fluidTank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public TankWidget(@Nullable IFluidTank fluidTank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(width, height));
        this.fluidTank = fluidTank;
        this.showAmount = true;
        this.allowClickFilled = allowClickContainerFilling;
        this.allowClickDrained = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget setFluidTank(IFluidTank fluidTank) {
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
            fluidTank.getFluid();
            this.lastFluidInTank = fluidTank.getFluid().copy();
        } else {
            this.lastFluidInTank = null;
        }
        this.lastTankCapacity = fluidTank != null ? fluidTank.getCapacity() : 0;
        return this;
    }

    public TankWidget setBackground(IGuiTexture background) {
        super.setBackground(background);
        return this;
    }

    @Override
    public Object getJEIIngredient() {
        if (lastFluidInTank == null) return null;
        if (LDLib.isReiLoaded()) {
            return lastFluidInTank.isEmpty() ? null : EntryStacks.of(FluidStackHooksForge.fromForge(lastFluidInTank));
        }
        return lastFluidInTank.isEmpty() ? null : lastFluidInTank;
    }

    @Override
    public IngredientIO getIngredientIO() {
        return ingredientIO;
    }

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
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        if (isClientSideWidget && fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluid();
            if (fluidTank.getCapacity() != lastTankCapacity) {
                this.lastTankCapacity = fluidTank.getCapacity();
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
                DrawerHelper.drawFluidForGui(matrixStack, lastFluidInTank, lastFluidInTank.getAmount(), (int) (x + drawnU * width), (int) (y + drawnV * height), ((int) (width * drawnWidth)), ((int) (height * drawnHeight)));
            }

            if (showAmount && !lastFluidInTank.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.scale(0.5F, 0.5F, 1);
                String s = TextFormattingUtil.formatLongToCompactStringBuckets(lastFluidInTank.getAmount(), 3) + "B";
                Font fontRenderer = Minecraft.getInstance().font;
                fontRenderer.drawShadow(matrixStack, s, (pos.x + (size.width / 3f)) * 2 - fontRenderer.width(s) + 21, (pos.y + (size.height / 3f) + 6) * 2, 0xFFFFFF);
                matrixStack.popPose();
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        if (overlay != null) {
            overlay.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInForeground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY)) {
            List<Component> tooltips = new ArrayList<>();
            if (lastFluidInTank != null && !lastFluidInTank.isEmpty()) {
                FluidAttributes fluid = lastFluidInTank.getFluid().getAttributes();
                tooltips.add(fluid.getDisplayName(lastFluidInTank));
                tooltips.add(new TranslatableComponent("ldlib.fluid.amount", lastFluidInTank.getAmount(), lastTankCapacity));
                tooltips.add(new TranslatableComponent("ldlib.fluid.temperature", fluid.getTemperature(lastFluidInTank)));
                tooltips.add(new TranslatableComponent(fluid.isGaseous(lastFluidInTank) ? "ldlib.fluid.state_gas" : "ldlib.fluid.state_liquid"));
            } else {
                tooltips.add(new TranslatableComponent("ldlib.fluid.empty"));
                tooltips.add(new TranslatableComponent("ldlib.fluid.amount", 0, lastTankCapacity));
            }
            if (gui != null) {
                tooltips.addAll(tooltipTexts);
                gui.getModularUIGui().setHoverTooltip(getToolTips(tooltips), ItemStack.EMPTY, null, null);
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        } else {
            super.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);
        }
        if (drawHoverOverlay && isMouseOverElement(mouseX, mouseY)) {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(matrixStack, getPosition().x + 1, getPosition().y + 1, getSize().width - 2, getSize().height - 2, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    public void detectAndSendChanges() {
        if (fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluid();
            if (fluidTank.getCapacity() != lastTankCapacity) {
                this.lastTankCapacity = fluidTank.getCapacity();
                writeUpdateInfo(0, buffer -> buffer.writeVarInt(lastTankCapacity));
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
                CompoundTag fluidStackTag = fluidStack.writeToNBT(new CompoundTag());
                writeUpdateInfo(2, buffer -> buffer.writeNbt(fluidStackTag));
            } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
                this.lastFluidInTank.setAmount(fluidStack.getAmount());
                writeUpdateInfo(3, buffer -> buffer.writeVarInt(lastFluidInTank.getAmount()));
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
            this.lastTankCapacity = fluidTank.getCapacity();
            buffer.writeVarInt(lastTankCapacity);
            FluidStack fluidStack = fluidTank.getFluid();
            this.lastFluidInTank = fluidStack.copy();
            buffer.writeNbt(fluidStack.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            this.lastTankCapacity = buffer.readVarInt();
            readUpdateInfo(2, buffer);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarInt();
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
        if (!currentStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) return -1;
        int maxAttempts = isShiftKeyDown ? currentStack.getCount() : 1;

        if (allowClickFilled && fluidTank.getFluidAmount() > 0) {
            boolean performedFill = false;
            FluidStack initialFluid = fluidTank.getFluid();
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidUtils.tryFillContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                currentStack = FluidUtils.tryFillContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, true).getResult();
                performedFill = true;
            }
            if (performedFill) {
                SoundEvent soundevent = initialFluid.getFluid().getAttributes().getFillSound(initialFluid);
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                gui.getModularUIContainer().setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        if (allowClickDrained) {
            boolean performedEmptying = false;
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidUtils.tryEmptyContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                currentStack = FluidUtils.tryEmptyContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, true).getResult();
                performedEmptying = true;
            }
            FluidStack filledFluid = fluidTank.getFluid();
            if (performedEmptying) {
                SoundEvent soundevent = filledFluid.getFluid().getAttributes().getEmptySound(filledFluid);
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                gui.getModularUIContainer().setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        return -1;
    }

    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((allowClickDrained || allowClickFilled) && isMouseOverElement(mouseX, mouseY)) {
            ItemStack currentStack = gui.getModularUIContainer().getCarried();
            if (button == 0 && currentStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                boolean isShiftKeyDown = isShiftDown();
                writeClientAction(1, writer -> writer.writeBoolean(isShiftKeyDown));
                playButtonClickSound();
                return true;
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
