package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.core.mixins.accessor.SlotAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.utils.CycleItemStackHandler;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@LDLRegister(name = "item_slot", group = "widget.container")
@Accessors(chain = true)
public class SlotWidget extends Widget implements IRecipeIngredientSlot, IConfigurableWidget {
    @Nullable
    protected static Slot HOVER_SLOT = null;
    @Nullable
    protected Slot slotReference;
    @Configurable
    @Setter
    protected boolean canTakeItems;
    @Configurable
    @Setter
    protected boolean canPutItems;
    public boolean isPlayerContainer;
    public boolean isPlayerHotBar;
    @Configurable
    @Setter
    public boolean drawHoverOverlay = true;
    @Configurable
    @Setter
    public boolean drawHoverTips = true;

    @Configurable
    @Setter
    protected IGuiTexture overlay;

    @Setter
    protected Runnable changeListener;
    @Setter
    protected BiConsumer<SlotWidget, List<Component>> onAddedTooltips;
    @Setter
    protected Function<ItemStack, ItemStack> itemHook;
    @Setter @Getter
    protected IngredientIO ingredientIO = IngredientIO.RENDER_ONLY;
    @Setter @Getter
    protected float XEIChance = 1f;
    @NotNull
    public List<Consumer<List<Component>>> tooltipCallbacks = new ArrayList<>();

    public SlotWidget() {
        super(new Position(0, 0), new Size(18, 18));
    }

    @Override
    public void initTemplate() {
        setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"));
        this.canTakeItems = true;
        this.canPutItems = true;
    }

    public SlotWidget(Container inventory, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        setContainerSlot(inventory, slotIndex);
    }

    public SlotWidget(IItemTransfer itemHandler, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        setHandlerSlot(itemHandler, slotIndex);
    }

    protected Slot createSlot(Container inventory, int index) {
        return new WidgetSlot(inventory, index, 0, 0);
    }

    protected Slot createSlot(IItemTransfer itemHandler, int index) {
        return new WidgetSlotItemTransfer(itemHandler, index, 0, 0);
    }

    public SlotWidget setContainerSlot(Container inventory, int slotIndex) {
        updateSlot(createSlot(inventory, slotIndex));
        return this;
    }

    public SlotWidget setHandlerSlot(IItemTransfer itemHandler, int slotIndex) {
        updateSlot(createSlot(itemHandler, slotIndex));
        return this;
    }

    protected void updateSlot(Slot slot) {
        if (this.slotReference != null && this.gui != null && !isClientSideWidget) {
            getGui().removeNativeSlot(this.slotReference);
        }
        this.slotReference = slot;
        if (this.gui != null && !isClientSideWidget) {
            getGui().addNativeSlot(this.slotReference, this);
        }
    }

    @Override
    public final void setSize(Size size) {
        // you cant modify size.
    }

    @Override
    public void setGui(ModularUI gui) {
        if (!isClientSideWidget && this.gui != gui) {
            if (this.gui != null && slotReference != null) {
                this.gui.removeNativeSlot(slotReference);
            }
            if (gui != null && slotReference != null) {
                gui.addNativeSlot(slotReference, this);
            }
        }
        super.setGui(gui);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInForeground(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (slotReference != null && drawHoverTips && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            ItemStack stack = slotReference.getItem();
            if (gui != null) {
                gui.getModularUIGui().setHoveredSlot(slotReference);
            }
            if (!stack.isEmpty() && gui != null) {
                List<Component> tips = new ArrayList<>(getToolTips(DrawerHelper.getItemToolTip(stack)));
                tips.addAll(tooltipTexts);
                gui.getModularUIGui().setHoverTooltip(tips, stack, null, stack.getTooltipImage().orElse(null));
            } else {
                super.drawInForeground(mStack, mouseX, mouseY, partialTicks);
            }
        } else {
            super.drawInForeground(mStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(mStack, mouseX, mouseY, partialTicks);
        Position pos = getPosition();
        if (slotReference != null)  {
            ItemStack itemStack = getRealStack(slotReference.getItem());
            ModularUIGuiContainer modularUIGui = gui == null ? null : gui.getModularUIGui();
            if (itemStack.isEmpty() && modularUIGui!= null && modularUIGui.getQuickCrafting() && modularUIGui.getQuickCraftSlots().contains(slotReference)) { // draw split
                int splitSize = modularUIGui.getQuickCraftSlots().size();
                itemStack = gui.getModularUIContainer().getCarried();
                if (!itemStack.isEmpty() && splitSize > 1 && AbstractContainerMenu.canItemQuickReplace(slotReference, itemStack, true)) {
                    itemStack = itemStack.copy();
                    AbstractContainerMenu.getQuickCraftSlotCount(modularUIGui.getQuickCraftSlots(), modularUIGui.dragSplittingLimit, itemStack, slotReference.getItem().isEmpty() ? 0 : slotReference.getItem().getCount());
                    int k = Math.min(itemStack.getMaxStackSize(), slotReference.getMaxStackSize(itemStack));
                    if (itemStack.getCount() > k) {
                        itemStack.setCount(k);
                    }
                }
            }
            if (!itemStack.isEmpty()) {
                DrawerHelper.drawItemStack(mStack, itemStack, pos.x + 1, pos.y + 1, -1, null);
            }
        }
        if (overlay != null) {
            overlay.draw(mStack, mouseX, mouseY, pos.x, pos.y, 18, 18);
        }
        if (drawHoverOverlay && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(mStack,getPosition().x + 1, getPosition().y + 1, 16, 16, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (slotReference != null && isMouseOverElement(mouseX, mouseY) && gui != null) {
            var stack = slotReference.getItem();
            if (!(canPutItems && stack.isEmpty() || canTakeItems && !stack.isEmpty())) return false;
            ModularUIGuiContainer modularUIGui = gui.getModularUIGui();
            boolean last = modularUIGui.getQuickCrafting();
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            HOVER_SLOT = slotReference;
            gui.getModularUIGui().superMouseClicked(mouseX, mouseY, button);
            HOVER_SLOT = null;
            if (last != modularUIGui.getQuickCrafting()) {
                modularUIGui.dragSplittingButton = button;
                if (button == 0) {
                    modularUIGui.dragSplittingLimit = 0;
                }
                else if (button == 1) {
                    modularUIGui.dragSplittingLimit = 1;
                }
                else if (Minecraft.getInstance().options.keyPickItem.matchesMouse(mouseKey.getValue())) {
                    modularUIGui.dragSplittingLimit = 2;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null) {
            HOVER_SLOT = slotReference;
            gui.getModularUIGui().superMouseReleased(mouseX, mouseY, button);
            HOVER_SLOT = null;
            return getIngredientIO() == IngredientIO.RENDER_ONLY && (canPutItems || canTakeItems);
        }
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null) {
            gui.getModularUIGui().superMouseDragged(mouseX, mouseY, button, dragX, dragY);
            return true;
        }
        return false;
    }

    @Override
    protected void onPositionUpdate() {
        if (gui != null) {
            Position position = getPosition();
            if (slotReference != null) {
                ((SlotAccessor)slotReference).setX(position.x + 1 - gui.getGuiLeft());
                ((SlotAccessor)slotReference).setY(position.y + 1 - gui.getGuiTop());
            }
        }
    }

    public SlotWidget(IItemTransfer itemHandler, int slotIndex, int xPosition, int yPosition) {
        this(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget(Container inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget setBackgroundTexture(IGuiTexture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public boolean canPutStack(ItemStack stack) {
        return isEnabled() && canPutItems;
    }

    public boolean canTakeStack(Player player) {
        return isEnabled() && canTakeItems;
    }

    public boolean isEnabled() {
        return this.isActive() && isVisible();
    }

    public boolean canMergeSlot(ItemStack stack) {
        return isEnabled();
    }

    public void onSlotChanged() {
        if (gui == null) return;
        gui.holder.markAsDirty();
    }

    public ItemStack slotClick(int dragType, ClickType clickTypeIn, Player player) {
        return null;
    }

    @Nullable
    public final Slot getHandle() {
        return slotReference;
    }

    public SlotWidget setLocationInfo(boolean isPlayerContainer, boolean isPlayerHotBar) {
        this.isPlayerHotBar = isPlayerHotBar;
        this.isPlayerContainer = isPlayerContainer;
        return this;
    }

    private List<Component> getToolTips(List<Component> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        for (Consumer<List<Component>> tooltipCallback : tooltipCallbacks) {
            tooltipCallback.accept(list);
        }
        return list;
    }

    @Override
    public void addTooltipCallback(Consumer<List<Component>> callback) {
        this.tooltipCallbacks.add(callback);
    }

    @Override
    public void clearTooltipCallback() {
        this.tooltipCallbacks.clear();
    }

    @Nullable
    @Override
    public Object getXEIIngredientOverMouse(double mouseX, double mouseY) {
        if (self().isMouseOverElement(mouseX, mouseY)) {
            if (slotReference == null || slotReference.getItem().isEmpty()) return null;
            if (LDLib.isReiLoaded()) {
                return EntryStacks.of(getRealStack(getHandle().getItem()));
            }
            if (LDLib.isEmiLoaded()) {
                return new ItemEmiStack(getRealStack(getHandle().getItem()));
            }
            return getRealStack(getHandle().getItem());
        }
        return null;
    }

    @Override
    public List<Object> getXEIIngredients() {
        if (slotReference == null || slotReference.getItem().isEmpty()) return Collections.emptyList();
        var handler = getHandle();
        if (handler == null) return Collections.emptyList();
        // if CycleItemStackHandler
        if (handler instanceof WidgetSlotItemTransfer widgetSlotItemTransfer && widgetSlotItemTransfer.itemHandler instanceof CycleItemStackHandler cycleItemStackHandler) {
            var stream = cycleItemStackHandler.getStackList(widgetSlotItemTransfer.index).stream().map(this::getRealStack);
            if (LDLib.isJeiLoaded()) {
                return stream.map(Object.class::cast).toList();
            }
            if (LDLib.isReiLoaded()) {
                return List.of(EntryIngredient.of(stream.map(EntryStacks::of).toList()));
            } else if (LDLib.isEmiLoaded()) {
                return List.of(new ListEmiIngredient(stream.map(ItemEmiStack::new).toList(), getRealStack(handler.getItem()).getCount()).setChance(getXEIChance()));
            }
        }

        if (LDLib.isJeiLoaded()) {
            return List.of(getRealStack(handler.getItem()));
        } else if (LDLib.isReiLoaded()) {
            return List.of(EntryStacks.of(getRealStack(handler.getItem())));
        } else if (LDLib.isEmiLoaded()) {
            return List.of(new ItemEmiStack(getRealStack(handler.getItem())));
        }
        return List.of(getRealStack(handler.getItem()));
    }

    public ItemStack getRealStack(ItemStack itemStack) {
        if (itemHook != null) return itemHook.apply(itemStack);
        return itemStack;
    }


    protected class WidgetSlot extends Slot {

        public WidgetSlot(Container inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(@Nonnull Player playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.mayPickup(playerIn);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
//            if(!SlotWidget.this.canPutStack(stack)) return;
            super.set(stack);
            if (changeListener != null) {
                changeListener.run();
            }
        }

        @Override
        public void setChanged() {
            SlotWidget.this.onSlotChanged();
        }

        @Override
        public boolean isActive() {
            return SlotWidget.this.isEnabled() && (HOVER_SLOT == null || HOVER_SLOT == this);
        }

    }

    protected class WidgetSlotItemTransfer extends Slot {
        private static final Container emptyInventory = new SimpleContainer(0);
        private final IItemTransfer itemHandler;
        private final int index;


        public WidgetSlotItemTransfer(IItemTransfer itemHandler, int index, int xPosition, int yPosition) {
            super(emptyInventory, index, xPosition, yPosition);
            this.itemHandler = itemHandler;
            this.index = index;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && (!stack.isEmpty() && this.itemHandler.isItemValid(this.index, stack));
        }

        @Override
        public boolean mayPickup(@Nullable Player playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && !this.itemHandler.extractItem(index, 1, true).isEmpty();
        }

        @Override
        @Nonnull
        public ItemStack getItem()
        {
            return this.itemHandler.getStackInSlot(index);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            this.itemHandler.setStackInSlot(index, stack);
            this.setChanged();
        }

        @Override
        public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {

        }

        @Override
        public int getMaxStackSize()
        {
            return this.itemHandler.getSlotLimit(this.index);
        }

        @Override
        public int getMaxStackSize(@Nonnull ItemStack stack) {
            ItemStack maxAdd = stack.copy();
            int maxInput = stack.getMaxStackSize();
            maxAdd.setCount(maxInput);
            ItemStack currentStack = this.itemHandler.getStackInSlot(index);
            this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
            ItemStack remainder = this.itemHandler.insertItem(index, maxAdd, true);
            this.itemHandler.setStackInSlot(index, currentStack);
            return maxInput - remainder.getCount();
        }

        @NotNull
        @Override
        public ItemStack remove(int amount) {
            var result = this.itemHandler.extractItem(index, amount, false);
            if (changeListener != null && !getItem().isEmpty()) {
                changeListener.run();
            }
            return result;
        }

        @Override
        public void setChanged() {
            this.itemHandler.onContentsChanged();
            if (changeListener != null) {
                changeListener.run();
            }
            SlotWidget.this.onSlotChanged();
        }

        @Override
        public boolean isActive() {
            return SlotWidget.this.isEnabled() && (HOVER_SLOT == null || HOVER_SLOT == this);
        }

        @Override
        public void initialize(ItemStack stack) {
            itemHandler.setStackInSlot(this.index, stack);
            this.setChanged();
        }
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        var handler = new ItemStackTransfer();
        handler.setStackInSlot(0, Blocks.STONE.asItem().getDefaultInstance());
        father.addConfigurators(new WrapperConfigurator("ldlib.gui.editor.group.preview", new SlotWidget(){
            @Override
            public void updateScreen() {
                super.updateScreen();
                setHoverTooltips(SlotWidget.this.tooltipTexts);
                this.backgroundTexture = SlotWidget.this.backgroundTexture;
                this.hoverTexture = SlotWidget.this.hoverTexture;
                this.drawHoverOverlay = SlotWidget.this.drawHoverOverlay;
                this.drawHoverTips = SlotWidget.this.drawHoverTips;
                this.overlay = SlotWidget.this.overlay;
            }
        }.setCanPutItems(false).setCanTakeItems(false).setHandlerSlot(handler, 0)));

        IConfigurableWidget.super.buildConfigurator(father);
    }
}
