package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.emi.emi.api.render.EmiRenderable;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote IGui2Renderable
 */
public interface IGui2Renderable {
    static EmiRenderable toDrawable(IGuiTexture guiTexture, int width, int height) {
        return (matrices, x, y, delta) -> {
            if (guiTexture == null) return;
            var graphics = new GuiGraphics(Minecraft.getInstance(), MultiBufferSource.immediate((Tesselator.getInstance().getBuilder())));
            graphics.pose().mulPoseMatrix(matrices.last().pose());
            guiTexture.draw(graphics, 0, 0, x, y, width, height);
        };
    }
}
