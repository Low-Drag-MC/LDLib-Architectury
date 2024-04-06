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
        return (graphics, bounds, mouseX, mouseY, delta) -> {
            if (guiTexture == null) return;
            guiTexture.draw(graphics, mouseX, mouseY, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getWidth());
        };
    }
}
