package com.lowdragmc.lowdraglib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class BlockPosFace {
    public final Direction facing;
    public final BlockPos pos;

    public BlockPosFace(BlockPos pos, Direction facing) {
        this.pos = pos;
        this.facing = facing;
    }

    @Override
    public boolean equals(@Nullable Object bp) {
        if (bp instanceof BlockPosFace) {
            return pos.equals(((BlockPosFace) bp).pos) && ((BlockPosFace) bp).facing == facing;
        }
        return super.equals(bp);
    }

    @Override
    public int hashCode() {
        return pos.hashCode() + facing.hashCode();
    }
}
