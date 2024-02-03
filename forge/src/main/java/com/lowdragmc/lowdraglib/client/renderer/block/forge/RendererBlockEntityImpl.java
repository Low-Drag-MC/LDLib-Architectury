package com.lowdragmc.lowdraglib.client.renderer.block.forge;

import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class RendererBlockEntityImpl {
    public static RegistryObject<BlockEntityType<RendererBlockEntity>> TYPE;

    public static BlockEntityType<?> TYPE() {
        return TYPE.get();
    }
}
