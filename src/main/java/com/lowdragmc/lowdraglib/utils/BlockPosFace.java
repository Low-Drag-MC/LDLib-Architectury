package com.lowdragmc.lowdraglib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public record BlockPosFace(BlockPos pos, Direction facing) {

    @Override
    public boolean equals(@Nullable Object other) {
        if (other instanceof BlockPosFace bp) {
            return pos.equals(bp.pos) && bp.facing == facing;
        }
        return false;
    }

}
