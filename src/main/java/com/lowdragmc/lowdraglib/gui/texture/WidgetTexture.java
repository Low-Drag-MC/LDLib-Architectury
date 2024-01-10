package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

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
    @OnlyIn(Dist.CLIENT)
    protected void drawInternal(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
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
        graphics.pose().pushPose();

        graphics.pose().translate(x + width / 2f, y + height / 2f, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().translate(-x + -width / 2f, -y + -height / 2f, 0);

        graphics.pose().translate(xOffset, yOffset, 0 );
        widget.drawInBackground(graphics, this.centerX, this.centerY, particleTick);
        widget.drawInForeground(graphics, this.centerX, this.centerY, particleTick);
        graphics.pose().popPose();

    }

}
