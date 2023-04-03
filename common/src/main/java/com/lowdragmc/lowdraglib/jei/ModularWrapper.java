package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ModularWrapper<T extends Widget> extends ModularUIGuiContainer {
    protected T widget;

    public ModularWrapper(T widget) {
        super(new ModularUI(widget.getSize().width, widget.getSize().height, IUIHolder.EMPTY, Minecraft.getInstance().player).widget(widget), -1);
        modularUI.initWidgets();
        this.minecraft = Minecraft.getInstance();
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.font;
        this.widget = widget;
    }

    private int lastTick;
    private int left, top;

    public T getWidget() {
        return widget;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public String getUid() {
        return null;
    }

    /**
     * For JEI to use
     */
    public void setRecipeLayout(int left, int top) {
        modularUI.initWidgets();
        this.left = left;
        this.top = top;
        this.width = minecraft.getWindow().getGuiScaledWidth();
        this.height = minecraft.getWindow().getGuiScaledHeight();
        modularUI.updateScreenSize(this.width, this.height);
        Position displayOffset = new Position(modularUI.getGuiLeft(), top);
        modularUI.mainGroup.setParentPosition(displayOffset);
//        this.menu.slots.clear();
    }

    /**
     * For REI to use
     */
    public void setRecipeWidget(int left, int top) {
        modularUI.initWidgets();
        this.left = 0;
        this.top = 0;
        this.width = minecraft.getWindow().getGuiScaledWidth();
        this.height = minecraft.getWindow().getGuiScaledHeight();
        modularUI.updateScreenSize(this.width, this.height);
        Position displayOffset = new Position(left, top);
        modularUI.mainGroup.setParentPosition(displayOffset);
    }

    public void setEmiRecipeWidget(int left, int top) {
        modularUI.initWidgets();
        this.left = left;
        this.top = top;
        this.width = minecraft.getWindow().getGuiScaledWidth();
        this.height = minecraft.getWindow().getGuiScaledHeight();
        modularUI.updateScreenSize(this.width, this.height);
        Position displayOffset = new Position(left, top);
        modularUI.mainGroup.setParentPosition(displayOffset);
    }

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (minecraft.player.tickCount != lastTick) {
            updateScreen();
            lastTick = minecraft.player.tickCount;
        }
        if (LDLib.isEmiLoaded()) {
            var viewStack = RenderSystem.getModelViewStack();
            viewStack.pushPose();
            viewStack.translate(-left, -top, 0);
            RenderSystem.applyModelViewMatrix();
            render(matrixStack, mouseX + left, mouseY + top, partialTicks);
            viewStack.popPose();
            RenderSystem.applyModelViewMatrix();
        } else {
            matrixStack.translate(-left, -top, 0);
            render(matrixStack, mouseX + left, mouseY + top, partialTicks);
            matrixStack.translate(left, top, 0);
        }
    }

    public void updateScreen() {
        modularUI.mainGroup.updateScreen();
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.hoveredSlot = null;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        tooltipTexts = null;
        tooltipFont = null;
        tooltipStack = ItemStack.EMPTY;
        tooltipComponent = null;

        modularUI.mainGroup.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        modularUI.mainGroup.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);

        if (tooltipTexts != null && tooltipTexts.size() > 0) {
            matrixStack.translate(0, 0, 200);
            renderTooltip(matrixStack, tooltipTexts, Optional.ofNullable(tooltipComponent), mouseX, mouseY);
            matrixStack.translate(0, 0, -200);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

}
