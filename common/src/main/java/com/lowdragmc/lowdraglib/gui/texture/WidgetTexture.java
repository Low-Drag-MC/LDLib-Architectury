package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote WidgetDraggingTexture
 */
public class WidgetTexture extends TransformTexture{
    private final Widget widget;
    private int centerX;
    private int centerY;
    private boolean isDragging;
    private boolean fixedCenter;

    public WidgetTexture(Widget widget) {
        this.widget = widget;
        this.centerX = widget.getPosition().x + widget.getSize().width / 2;
        this.centerY = widget.getPosition().y + widget.getSize().height / 2;
    }

    public WidgetTexture(int mouseX, int mouseY, Widget widget) {
        this.widget = widget;
        this.centerX = mouseX;
        this.centerY = mouseY;
        this.isDragging = true;
        this.fixedCenter = true;
    }

    public WidgetTexture setDragging(boolean dragging) {
        isDragging = dragging;
        return this;
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        int xOffset;
        int yOffset;
        float scale  = 1;
        if (!fixedCenter) {
            this.centerX = widget.getPosition().x + widget.getSize().width / 2;
            this.centerY = widget.getPosition().y + widget.getSize().height / 2;
        }
        if (isDragging) {
            xOffset = mouseX - this.centerX;
            yOffset = mouseY - this.centerY;
        } else {
            xOffset = (int) (x + width / 2 - this.centerX);
            yOffset = (int) (y + height / 2 - this.centerY);
            float scaleW = width * 1f / widget.getSize().width;
            float scaleH = height * 1f / widget.getSize().height;
            scale = Math.min(scaleW, scaleH);
        }
        float particleTick = Minecraft.getInstance().getFrameTime();
        stack.pushPose();

        stack.translate(x + width / 2f, y + height / 2f, 0);
        stack.scale(scale, scale, 1);
        stack.translate(-x + -width / 2f, -y + -height / 2f, 0);

        stack.translate(xOffset, yOffset, 0 );
        widget.drawInBackground(stack, this.centerX, this.centerY, particleTick);
        widget.drawInForeground(stack, this.centerX, this.centerY, particleTick);
        stack.popPose();

    }

}
