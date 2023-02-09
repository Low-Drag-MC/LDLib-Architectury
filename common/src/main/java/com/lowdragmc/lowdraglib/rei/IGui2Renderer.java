package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote IGui2Renderer
 */
public interface IGui2Renderer {
    static Renderer toDrawable(IGuiTexture guiTexture) {
        return new Renderer() {
            int z = 0;
            @Override
            public void render(PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
                if (guiTexture == null) return;
                matrices.pushPose();
                matrices.translate(0, 0, z);
                guiTexture.draw(matrices, mouseX, mouseY, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getWidth());
                matrices.popPose();
            }

            @Override
            public int getZ() {
                return z;
            }

            @Override
            public void setZ(int z) {
                this.z = z;
            }
        };
    }
}
