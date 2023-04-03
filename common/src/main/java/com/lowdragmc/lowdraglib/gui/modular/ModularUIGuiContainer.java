package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.core.mixins.accessor.AbstractContainerScreenAccessor;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import com.lowdragmc.lowdraglib.side.ForgeEventHooks;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.screen.EmiScreenManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ModularUIGuiContainer extends AbstractContainerScreen<ModularUIContainer> {

    public final ModularUI modularUI;
    public Widget lastFocus;
    public boolean focused;
    public int dragSplittingLimit;
    public int dragSplittingButton;
    // hover tips
    protected List<Component> tooltipTexts;
    protected TooltipComponent tooltipComponent;
    protected Font tooltipFont;
    protected ItemStack tooltipStack = ItemStack.EMPTY;
    // drag element
    protected Tuple<Object, IGuiTexture> draggingElement;

    public ModularUIGuiContainer(ModularUI modularUI, int windowId) {
        super(new ModularUIContainer(modularUI, windowId), modularUI.entityPlayer.getInventory(), Component.nullToEmpty("modularUI"));
        this.modularUI = modularUI;
        modularUI.setModularUIGui(this);
    }

    public void setHoverTooltip(List<Component> tooltipTexts, ItemStack tooltipStack, @Nullable Font tooltipFont, @Nullable TooltipComponent tooltipComponent) {
        this.tooltipTexts = tooltipTexts;
        this.tooltipStack = tooltipStack;
        this.tooltipFont = tooltipFont;
        this.tooltipComponent = tooltipComponent;
    }

    public boolean setDraggingElement(Object element, IGuiTexture renderer) {
        if (draggingElement != null) return false;
        draggingElement = new Tuple<>(element, renderer);
        return true;
    }

    @Nullable
    public Object getDraggingElement() {
        if (draggingElement == null) return null;
        return draggingElement.getA();
    }

    @Override
    public void init() {
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
        this.imageWidth = modularUI.getWidth();
        this.imageHeight = modularUI.getHeight();
        super.init();
        this.modularUI.updateScreenSize(width, height);
    }

    @Override
    public void removed() {
        super.removed();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (modularUI.holder.isInvalid() && modularUI.entityPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
        modularUI.mainGroup.updateScreen();
        modularUI.addTick();
    }

    public void handleWidgetUpdate(SPacketUIWidgetUpdate packet) {
        if (packet.windowId == getMenu().containerId) {
            int updateId = packet.updateData.readVarInt();
            modularUI.mainGroup.readUpdateInfo(updateId, packet.updateData);
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.hoveredSlot = null;
        
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        tooltipTexts = null;
        tooltipComponent = null;

        this.renderBackground(poseStack);
        if (Platform.isForge()) ForgeEventHooks.postBackgroundRenderedEvent(this, poseStack);

        modularUI.mainGroup.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        if (Platform.isForge()) ForgeEventHooks.postRenderBackgroundEvent(this, poseStack, mouseX, mouseY);

        if (LDLib.isEmiLoaded()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            EmiScreenManager.render(poseStack, mouseX, mouseY, partialTicks);
        }

        modularUI.mainGroup.drawInForeground(poseStack, mouseX, mouseY, partialTicks);

        if (draggingElement != null) {
            draggingElement.getB().draw(poseStack, mouseX, mouseY, mouseX - 20, mouseY - 20, 40, 40);
        } else if (tooltipTexts != null && tooltipTexts.size() > 0) {
            poseStack.translate(0, 0, 200);
            renderTooltip(poseStack, tooltipTexts, Optional.ofNullable(tooltipComponent), mouseX, mouseY);
            poseStack.translate(0, 0, -200);
        }

        RenderSystem.depthMask(true);

        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(leftPos, topPos, 0.0D);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hoveredSlot = null;

        if (Platform.isForge()) ForgeEventHooks.postRenderForegroundEvent(this, poseStack, mouseX, mouseY);

        renderItemStackOnMouse(mouseX, mouseY);
        renderReturningItemStack();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    public void setHoveredSlot(Slot hoveredSlot) {
        this.hoveredSlot = hoveredSlot;
    }

    private void renderItemStackOnMouse(int mouseX, int mouseY) {
        if (minecraft == null || minecraft.player == null) return;
        ItemStack draggedStack = ((AbstractContainerScreenAccessor)this).getDraggingItem();
        ItemStack itemstack = draggedStack.isEmpty() ? getMenu().getCarried() : draggedStack;
        if (!itemstack.isEmpty()) {
            int k2 = draggedStack.isEmpty() ? 8 : 16;
            String s = null;
            if (!draggedStack.isEmpty() && ((AbstractContainerScreenAccessor)this).isSplittingStack()) {
                itemstack = itemstack.copy();
                itemstack.setCount((int) Math.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(((AbstractContainerScreenAccessor)this).getQuickCraftingRemainder());
                if (itemstack.isEmpty()) {
                    s = "" + ChatFormatting.YELLOW + "0";
                }
            }
            this.renderFloatingItem(itemstack, mouseX - leftPos - 8, mouseY - topPos - k2, s);
        }
        
    }

    public void renderFloatingItem(ItemStack stack, int pX, int pY, @Nullable String text) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.translate(0.0D, 0.0D, 232.0D);
        RenderSystem.applyModelViewMatrix();
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0F;
        Font font = Minecraft.getInstance().font;
        this.itemRenderer.renderAndDecorateItem(stack, pX, pY);
        this.itemRenderer.renderGuiItemDecorations(font, stack, pX, pY - (((AbstractContainerScreenAccessor)this).getDraggingItem().isEmpty() ? 0 : 8), text);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }

    private void renderReturningItemStack() {
        if (!((AbstractContainerScreenAccessor)this).getSnapbackItem().isEmpty()) {
            float f = (float)(Util.getMillis() - ((AbstractContainerScreenAccessor)this).getSnapbackTime()) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                ((AbstractContainerScreenAccessor)this).setSnapbackItem(ItemStack.EMPTY);
            }

            int l2 = ((AbstractContainerScreenAccessor)this).getSnapbackEnd().x - ((AbstractContainerScreenAccessor)this).getSnapbackStartX();
            int i3 = ((AbstractContainerScreenAccessor)this).getSnapbackEnd().y - ((AbstractContainerScreenAccessor)this).getSnapbackStartY();
            int l1 = ((AbstractContainerScreenAccessor)this).getSnapbackStartX() + (int)((float)l2 * f);
            int i2 = ((AbstractContainerScreenAccessor)this).getSnapbackStartY() + (int)((float)i3 * f);
            this.renderFloatingItem(((AbstractContainerScreenAccessor)this).getSnapbackItem(), l1, i2, null);
        }
    }

    public boolean switchFocus(@Nonnull Widget widget) {
        if (focused) return false;
        focused = true;
        if (lastFocus == widget) return false;
        Widget l = lastFocus;
        lastFocus = widget;
        if (l != null) l.setFocus(false);
        return true;
    }

    public Set<Slot> getQuickCraftSlots() {
        return this.quickCraftSlots;
    }

    public boolean getQuickCrafting() {
        return this.isQuickCrafting;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        focused = false;
        if (modularUI.mainGroup.mouseClicked(mouseX, mouseY, pButton)) return true;
        for (GuiEventListener guiEventListener : this.children()) {
            if (!guiEventListener.mouseClicked(mouseX, mouseY, pButton)) continue;
            this.setFocused(guiEventListener);
            if (pButton == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pButton, double pDragX, double pDragY) {
        focused = false;
        if (modularUI.mainGroup.mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY)) return true;
        if (this.getFocused() != null && this.isDragging() && pButton == 0) {
            return this.getFocused().mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
        focused = false;
        var result = modularUI.mainGroup.mouseReleased(mouseX, mouseY, pButton);
        draggingElement = null;
        if (result) return true;
        this.setDragging(false);
        return this.getChildAt(mouseX, mouseY).filter(guiEventListener -> guiEventListener.mouseReleased(mouseX, mouseY, pButton)).isPresent();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        focused = false;
        if (modularUI.mainGroup.keyPressed(keyCode, scanCode, modifiers)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        focused = false;
        if (modularUI.mainGroup.mouseWheelMove(mouseX, mouseY, wheelDelta)) return true;
        return this.getChildAt(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, wheelDelta)).isPresent();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        focused = false;
        if (modularUI.mainGroup.keyReleased(keyCode, scanCode, modifiers)) return true;
        return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        focused = false;
        if (modularUI.mainGroup.charTyped(codePoint, modifiers)) return true;
        return this.getFocused() != null && this.getFocused().charTyped(codePoint, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        focused = false;
        modularUI.mainGroup.mouseMoved(mouseX, mouseY);
    }

    public void superMouseClicked(double mouseX, double mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception ignored) { }
    }

    public void superMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public void superMouseReleased(double mouseX, double mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public boolean superKeyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean superMouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        return super.mouseScrolled(mouseX, mouseY, wheelDelta);
    }

    public boolean superKeyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean superCharTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    public void superMouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTicks, int pX, int pY) {
        
    }

    public List<Rect2i> getGuiExtraAreas() {
        return modularUI.mainGroup.getGuiExtraAreas(modularUI.mainGroup.toRectangleBox(), new ArrayList<>());
    }
}
