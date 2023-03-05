package com.lowdragmc.lowdraglib.client.scene.fabric;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author KilaBash
 * @date 2023/3/5
 * @implNote WorldSceneRendererImpl
 */
public class WorldSceneRendererImpl {

    public static boolean canRenderInLayer(BlockState state, RenderType renderType) {
        return ItemBlockRenderTypes.getChunkRenderType(state) == renderType;
    }

}
