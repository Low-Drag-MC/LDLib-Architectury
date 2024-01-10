package com.lowdragmc.lowdraglib.client.scene;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Scene Render State hooks.
 * This is where you decide whether this group of pos should be rendered. What other requirements do you have for rendering.
 */
public interface ISceneBlockRenderHook {
    default void apply(boolean isTESR, RenderType layer) {

    }

    default void applyBESR(Level world, BlockPos pos, BlockEntity blockEntity, PoseStack poseStack, float partialTicks) {

    }

    default void applyVertexConsumerWrapper(Level world, BlockPos pos, BlockState state, WorldSceneRenderer.VertexConsumerWrapper wrapperBuffer, RenderType layer, float partialTicks) {

    }
}
