package com.lowdragmc.lowdraglib.gui.compass.component.animation;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.TransformTexture;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * @author KilaBash
 * @date 2023/7/30
 * @implNote TooltipBGTexture
 */
public class TooltipBGTexture extends TransformTexture {
    public static TooltipBGTexture INSTANCE = new TooltipBGTexture();

    @Override
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        ColorPattern.BLACK.rectTexture().draw(stack, mouseX, mouseY, x - 1, y - 1, width + 2, height + 2);
        ColorPattern.WHITE.borderTexture(1).draw(stack, mouseX, mouseY, x - 1, y - 1, width + 2, height + 2);
    }
}
