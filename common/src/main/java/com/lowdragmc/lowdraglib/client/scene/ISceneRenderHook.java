package com.lowdragmc.lowdraglib.client.scene;

import net.minecraft.client.renderer.RenderType;

/**
 * Scene Render State hooks.
 * This is where you decide whether this group of pos should be rendered. What other requirements do you have for rendering.
 */
public interface ISceneRenderHook {
    void apply(boolean isTESR, RenderType layer);
}
