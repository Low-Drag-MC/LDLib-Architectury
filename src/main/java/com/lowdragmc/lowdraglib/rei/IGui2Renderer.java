package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.minecraft.client.gui.GuiGraphics;

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
            public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
                if (guiTexture == null) return;
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, z);
                guiTexture.draw(graphics, mouseX, mouseY, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getWidth());
                graphics.pose().popPose();
            }
        };
    }
}
