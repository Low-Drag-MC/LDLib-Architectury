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
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.*;
import com.lowdragmc.lowdraglib.utils.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.common.input.ClickableIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.ingredients.TypedIngredient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LDLRegister(name = "fluid_slot", group = "widget.container")
@Accessors(chain = true)
public class TankWidget extends Widget implements IRecipeIngredientSlot, IConfigurableWidget {
    public final static ResourceBorderTexture FLUID_SLOT_TEXTURE = new ResourceBorderTexture("ldlib:textures/gui/fluid_slot.png", 18, 18, 1, 1);

    @Nullable
    @Getter
    protected IFluidTransfer fluidTank;
    @Getter
    protected int tank;
    @Configurable(name = "ldlib.gui.editor.name.showAmount")
    @Setter
    protected boolean showAmount;
    @Configurable(name = "ldlib.gui.editor.name.allowClickFilled")
    @Setter
    protected boolean allowClickFilled;
    @Configurable(name = "ldlib.gui.editor.name.allowClickDrained")
    @Setter
    protected boolean allowClickDrained;
    @Configurable(name = "ldlib.gui.editor.name.drawHoverOverlay")
    @Setter
    public boolean drawHoverOverlay = true;
    @Configurable(name = "ldlib.gui.editor.name.drawHoverTips")
    @Setter
    protected boolean drawHoverTips;
    @Configurable(name = "ldlib.gui.editor.name.fillDirection")
    @Setter
    protected ProgressTexture.FillDirection fillDirection = ProgressTexture.FillDirection.ALWAYS_FULL;
    @Configurable(name = "ldlib.gui.editor.name.overlayTexture")
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
        setBackground(FLUID_SLOT_TEXTURE);
        setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
    }

    public TankWidget(IFluidStorage fluidTank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this(fluidTank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public TankWidget(@Nullable IFluidStorage fluidTank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(width, height));
        this.fluidTank = fluidTank;
        this.tank = 0;
        this.showAmount = true;
        this.allowClickFilled = allowClickContainerFilling;
        this.allowClickDrained = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget(IFluidTransfer fluidTank, int tank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this(fluidTank, tank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public TankWidget(@Nullable IFluidTransfer fluidTank, int tank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(width, height));
        this.fluidTank = fluidTank;
        this.tank = tank;
        this.showAmount = true;
        this.allowClickFilled = allowClickContainerFilling;
        this.allowClickDrained = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget setFluidTank(IFluidStorage fluidTank) {
        this.fluidTank = fluidTank;
        if (isClientSideWidget) {
            setClientSideWidget();
        }
        return this;
    }

    public TankWidget setFluidTank(IFluidTransfer fluidTank, int tank) {
        this.fluidTank = fluidTank;
        this.tank = tank;
        if (isClientSideWidget) {
            setClientSideWidget();
        }
        return this;
    }

    @Override
    public TankWidget setClientSideWidget() {
        super.setClientSideWidget();
        if (fluidTank != null) {
            this.lastFluidInTank = fluidTank.getFluidInTank(tank).copy();
        } else {
            this.lastFluidInTank = null;
        }
        this.lastTankCapacity = fluidTank != null ? fluidTank.getTankCapacity(tank) : 0;
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

            if (this.fluidTank instanceof CycleFluidTransfer cycleItemStackHandler) {
                return getXEIIngredientsFromCycleTransfer(cycleItemStackHandler, tank);
            } else if (this.fluidTank instanceof TagOrCycleFluidTransfer transfer) {
                return getXEIIngredientsFromTagOrCycleTransfer(transfer, tank);
            }

            if (LDLib.isJeiLoaded()) {
                return JEICallWrapper.getPlatformFluidTypeForJEI(lastFluidInTank, getPosition(), getSize());
            }
            if (LDLib.isReiLoaded()) {
                return EntryStacks.of(dev.architectury.fluid.FluidStack.create(lastFluidInTank.getFluid(), lastFluidInTank.getAmount(), lastFluidInTank.getTag()));
            }
            if (LDLib.isEmiLoaded()) {
                return EmiStack.of(lastFluidInTank.getFluid(), lastFluidInTank.getTag(), lastFluidInTank.getAmount()).setChance(XEIChance);
            }
        }
        return null;
    }

    @Override
    public List<Object> getXEIIngredients() {
        if (lastFluidInTank == null || lastFluidInTank.isEmpty()) return Collections.emptyList();
        if (LDLib.isJeiLoaded()) {
            return List.of(JEICallWrapper.getPlatformFluidTypeForJEI(lastFluidInTank, getPosition(), getSize()));
        }
        if (LDLib.isReiLoaded()) {
            return List.of(EntryStacks.of(dev.architectury.fluid.FluidStack.create(lastFluidInTank.getFluid(), lastFluidInTank.getAmount(), lastFluidInTank.getTag())));
        }
        if (LDLib.isEmiLoaded()) {
            return List.of(EmiStack.of(lastFluidInTank.getFluid(), lastFluidInTank.getTag(), lastFluidInTank.getAmount()).setChance(XEIChance));
        }
        return List.of(FluidHelper.toRealFluidStack(lastFluidInTank));
    }

    private List<Object> getXEIIngredientsFromCycleTransfer(CycleFluidTransfer transfer, int index) {
        var stream = transfer.getStackList(index).stream();
        if (LDLib.isJeiLoaded()) {
            return stream.filter(fluid -> !fluid.isEmpty()).map(fluid -> JEICallWrapper.getPlatformFluidTypeForJEI(fluid, getPosition(), getSize())).toList();
        } else if (LDLib.isReiLoaded()) {
            return REICallWrapper.getReiIngredients(stream);
        } else if (LDLib.isEmiLoaded()) {
            return EMICallWrapper.getEmiIngredients(stream, getXEIChance());
        }
        return null;
    }

    private List<Object> getXEIIngredientsFromTagOrCycleTransfer(TagOrCycleFluidTransfer transfer, int index) {
        Either<Pair<List<TagKey<Fluid>>, Long>, List<FluidStack>> either = transfer
                .getStacks()
                .get(index);
        var ref = new Object() {
            List<Object> returnValue = null;
        };
        either.ifLeft(pair -> {
            List<TagKey<Fluid>> tags = pair.getFirst();
            long count = pair.getSecond();
            if (LDLib.isJeiLoaded()) {
                ref.returnValue = tags.stream()
                        .flatMap(tag -> BuiltInRegistries.FLUID
                                .getTag(tag)
                                .stream()
                                .flatMap(HolderSet.ListBacked::stream)
                                .map(fluid -> JEICallWrapper.getPlatformFluidTypeForJEI(FluidStack.create(fluid.value(), count), getPosition(), getSize())))
                        .collect(Collectors.toList());
            } else if (LDLib.isReiLoaded()) {
                ref.returnValue = REICallWrapper.getReiIngredients(tags, count);
            } else if (LDLib.isEmiLoaded()) {
                ref.returnValue = EMICallWrapper.getEmiIngredients(tags, count, getXEIChance());
            }
        }).ifRight(fluids -> {
            var stream = fluids.stream();
            if (LDLib.isJeiLoaded()) {
                ref.returnValue = stream.filter(fluid -> !fluid.isEmpty()).map(fluid -> JEICallWrapper.getPlatformFluidTypeForJEI(fluid, getPosition(), getSize())).toList();
            } else if (LDLib.isReiLoaded()) {
                ref.returnValue = REICallWrapper.getReiIngredients(stream);
            } else if (LDLib.isEmiLoaded()) {
                ref.returnValue = EMICallWrapper.getEmiIngredients(stream, getXEIChance());
            }
        });
        return ref.returnValue;
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
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        if (isClientSideWidget && fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluidInTank(tank);
            long capacity = fluidTank.getTankCapacity(tank);
            if (capacity != lastTankCapacity) {
                this.lastTankCapacity = capacity;
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
    @Environment(EnvType.CLIENT)

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
            FluidStack fluidStack = fluidTank.getFluidInTank(tank);
            long capacity = fluidTank.getTankCapacity(tank);
            if (capacity != lastTankCapacity) {
                this.lastTankCapacity = capacity;
                writeUpdateInfo(0, buffer -> buffer.writeVarLong(lastTankCapacity));
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
                CompoundTag fluidStackTag = fluidStack.saveToTag(new CompoundTag());
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
            this.lastTankCapacity = fluidTank.getTankCapacity(tank);
            buffer.writeVarLong(lastTankCapacity);
            FluidStack fluidStack = fluidTank.getFluidInTank(tank);
            this.lastFluidInTank = fluidStack.copy();
            buffer.writeNbt(fluidStack.saveToTag(new CompoundTag()));
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
    @Environment(EnvType.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarLong();
        } else if (id == 1) {
            this.lastFluidInTank = null;
        } else if (id == 2) {
            this.lastFluidInTank = FluidStack.loadFromTag(buffer.readNbt());
        } else if (id == 3 && lastFluidInTank != null) {
            this.lastFluidInTank.setAmount(buffer.readVarLong());
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
        FluidStack initialFluid = fluidTank.getFluidInTank(tank);
        if (allowClickFilled && initialFluid.getAmount() > 0) {
            boolean performedFill = false;
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
            var filledFluid = fluidTank.getFluidInTank(tank);
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

    @Environment(EnvType.CLIENT)
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
        var handler = new FluidStorage(5000);
        handler.fill(FluidStack.create(Fluids.WATER, 3000), false);
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

    /**
     * Wrapper for methods that use JEI classes so that classloading doesn't brick itself.
     */
    public static final class JEICallWrapper {
        public static Object getPlatformFluidTypeForJEI(FluidStack fluidStack, Position pos, Size size) {
            return _getPlatformFluidTypeForJEI(JEIPlugin.jeiRuntime.getJeiHelpers().getPlatformFluidHelper(), fluidStack, pos, size);
        }

        private static <T> Object _getPlatformFluidTypeForJEI(IPlatformFluidHelper<T> helper, FluidStack fluidStack, Position pos, Size size) {
            T ingredient = helper.create(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
            return new ClickableIngredient<>(TypedIngredient.createUnvalidated(helper.getFluidIngredientType(), ingredient),
                    new ImmutableRect2i(pos.x, pos.y, size.width, size.height));
        }
    }

    public static final class REICallWrapper {
        public static List<Object> getReiIngredients(Stream<FluidStack> stream) {
            return List.of(EntryIngredient.of(stream
                    .map(fluidStack -> dev.architectury.fluid.FluidStack.create(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag()))
                    .map(EntryStacks::of)
                    .toList()));
        }
        public static List<Object> getReiIngredients(List<TagKey<Fluid>> tags, long count) {
            //noinspection unchecked
            return (List<Object>) (List<?>) EntryIngredients.ofTags(tags, holder -> EntryStacks.of(dev.architectury.fluid.FluidStack.create(holder.value(), count)));
        }
    }

    public static final class EMICallWrapper {
        public static List<Object> getEmiIngredients(Stream<FluidStack> stream, float xeiChance) {
            return List.of(EmiIngredient.of(stream.map(fluidStack -> EmiStack.of(fluidStack.getFluid(), fluidStack.getTag(), fluidStack.getAmount())).toList()).setChance(xeiChance));
        }
        public static List<Object> getEmiIngredients(List<TagKey<Fluid>> tags, long count, float xeiChance) {
            return tags.stream()
                    .map(tag -> EmiIngredient.of(tag, count).setChance(xeiChance))
                    .collect(Collectors.toList());
        }
    }
}
