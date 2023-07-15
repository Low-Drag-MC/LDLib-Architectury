package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author KilaBash
 * @date 2022/12/5
 * @implNote TransformTexture
 */
@Configurable(name = "ldlib.gui.editor.group.transform")
public abstract class TransformTexture implements IGuiTexture{
    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 1)
    protected float xOffset;

    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 1)
    protected float yOffset;

    @Configurable
    @NumberRange(range = {0, Float.MAX_VALUE})
    protected float scale = 1;

    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 5)
    protected float rotation;

    public TransformTexture rotate(float degree) {
        rotation = degree;
        return this;
    }

    public TransformTexture scale(float scale) {
        this.scale = scale;
        return this;
    }

    public TransformTexture transform(float xOffset, float yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    @Environment(EnvType.CLIENT)
    protected void preDraw(PoseStack stack, float x, float y, float width, float height) {
        stack.pushPose();
        stack.translate(xOffset, yOffset, 0);

        stack.translate(x + width / 2f, y + height / 2f, 0);
        stack.scale(scale, scale, 1);
        stack.mulPose(new Quaternion(0, 0, rotation, true));
        stack.translate(-x + -width / 2f, -y + -height / 2f, 0);
    }

    @Environment(EnvType.CLIENT)
    protected void postDraw(PoseStack stack, float x, float y, float width, float height) {
        stack.popPose();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        preDraw(stack, x, y, width, height);
        drawInternal(stack, mouseX, mouseY, x, y, width, height);
        postDraw(stack, x, y, width, height);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void drawSubArea(PoseStack stack, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        preDraw(stack, x, y, width, height);
        drawSubAreaInternal(stack, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        postDraw(stack, x, y, width, height);
    }

    @Environment(EnvType.CLIENT)
    protected abstract void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height);

    @Environment(EnvType.CLIENT)
    protected void drawSubAreaInternal(PoseStack stack, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        drawInternal(stack, 0, 0, x, y, (int) width, (int) height);
    }

}
