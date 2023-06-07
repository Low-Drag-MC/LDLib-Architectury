package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.*;

@LDLRegister(name = "color_rect_texture", group = "texture")
@Accessors(chain = true)
public class ColorRectTexture extends TransformTexture{

    @Configurable
    @NumberColor
    @Setter
    public int color;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLT;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLB;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRT;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRB;

    public ColorRectTexture() {
        this(0x4f0ffddf);
    }

    public ColorRectTexture(int color) {
        this.color = color;
    }

    public ColorRectTexture(Color color) {
        this.color = color.getRGB();
    }

    public ColorRectTexture setRadius(float radius) {
        this.radiusLB = radius;
        this.radiusRT = radius;
        this.radiusRB = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectTexture setLeftRadius(float radius) {
        this.radiusLB = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectTexture setRightRadius(float radius) {
        this.radiusRT = radius;
        this.radiusRB = radius;
        return this;
    }

    public ColorRectTexture setTopRadius(float radius) {
        this.radiusRT = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectTexture setBottomRadius(float radius) {
        this.radiusLB = radius;
        this.radiusRB = radius;
        return this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (width == 0 || height == 0) return;
        if (radiusLT > 0 || radiusLB > 0 || radiusRT > 0 || radiusRB > 0) {
            float radius = Math.min(width, height) / 2f;
            DrawerHelper.drawRoundBox(stack, Rect.ofRelative((int) x, width, (int) y, height),
                    new Vector4f(Math.min(radius, radiusRT), Math.min(radiusRB, radius), Math.min(radius, radiusLT), Math.min(radius, radiusLB)), color);
        } else {
            DrawerHelper.drawSolidRect(stack, (int) x, (int) y, width, height, color);
        }
    }
}
