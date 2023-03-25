package com.lowdragmc.lowdraglib.client.model.custommodel;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2023/3/24
 * @implNote ICTMPredicate
 */
public interface ICTMPredicate {

    /**
     * If two blocks are adjacent and have same connected id, they can be regarded as connected.
     */
    @Nullable
    default ResourceLocation getConnectedID() {
        return null;
    }

    /**
     * Can texture connected to model.
     * @param coreState core block
     * @param adjacentState checking state
     */
    default boolean isConnected(BlockAndTintGetter level, BlockPos corePos, BlockState coreState, BlockPos adjacentPos, BlockState adjacentState, Direction side) {
        var adjacentID = getConnectedID();
        if (adjacentID != null) {
            var corePredicate = getPredicate(coreState);
            if (corePredicate != null) {
                return adjacentID.equals(corePredicate.getConnectedID());
            }
        }
        return coreState == adjacentState;
    }

    @Nullable
    static ICTMPredicate getPredicate(BlockState state) {
        if (state.getBlock() instanceof ICTMPredicate predicate) {
            return predicate;
        } else if (state.getBlock() instanceof IBlockRendererProvider rendererProvider && rendererProvider.getRenderer(state) instanceof ICTMPredicate predicate) {
            return predicate;
        }
        return null;
    }
}
