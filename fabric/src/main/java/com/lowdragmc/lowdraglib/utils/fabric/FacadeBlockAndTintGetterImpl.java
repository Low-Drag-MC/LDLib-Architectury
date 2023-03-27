package com.lowdragmc.lowdraglib.utils.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author KilaBash
 * @date 2023/3/27
 * @implNote FacadeBlockAndTintGetterImpl
 */
public class FacadeBlockAndTintGetterImpl {
    public static BlockState getAppearance(BlockState state, BlockAndTintGetter renderView, BlockPos pos, Direction side, @org.jetbrains.annotations.Nullable BlockState sourceState, @org.jetbrains.annotations.Nullable BlockPos sourcePos) {
        return state.getAppearance(renderView, pos, side, sourceState, sourcePos);
    }

}
