package com.lowdragmc.lowdraglib.gui.compass.component.animation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * @author KilaBash
 * @date 2023/7/29
 * @implNote BlockAnima
 */
public record BlockAnima(BlockPos pos, Vec3 offset, int duration) {
    public BlockAnima(BlockPos pos, Vec3 animaPos) {
        this(pos, animaPos, 15);
    }

    public BlockAnima(BlockPos pos) {
        this(pos, new Vec3(0, 0.7, 0));
    }
}
