package com.lowdragmc.lowdraglib.client.scene.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote WorldSceneRendererImpl
 */
public class WorldSceneRendererImpl {

    private static void renderBlocksForge(BlockRenderDispatcher blockRenderDispatcher, BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, RandomSource random, RenderType renderType) {
        var te = level.getBlockEntity(pos);
        blockRenderDispatcher.renderBatched(state, pos, level, poseStack, consumer, false, random, te == null ? ModelData.EMPTY : te.getModelData(), renderType, false);
    }

}
