package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.render.EmiRenderable;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote IGui2Renderable
 */
public interface IGui2Renderable {
    static EmiRenderable toDrawable(IGuiTexture guiTexture, int width, int height) {
        return (matrices, x, y, delta) -> {
            if (guiTexture == null) return;
            guiTexture.draw(matrices, 0, 0, x, y, width, height);
        };
    }
}
