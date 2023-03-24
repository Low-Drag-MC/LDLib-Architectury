package com.lowdragmc.lowdraglib.client.model.custommodel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author KilaBash
 * @date 2023/3/24
 * @implNote ICTMPredicate
 */
public interface ICTMPredicate {
    boolean isConnected(BlockAndTintGetter level, BlockPos pos, BlockState state, BlockState adjacent, Direction side);
}
