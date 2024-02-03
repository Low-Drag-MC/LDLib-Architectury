package com.lowdragmc.lowdraglib.client.renderer.block.fabric;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class RendererBlockEntityImpl {
    public static BlockEntityType<?> TYPE;

    public static BlockEntityType<?> TYPE() {
        return TYPE;
    }
}
