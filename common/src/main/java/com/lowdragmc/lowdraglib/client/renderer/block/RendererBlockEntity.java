package com.lowdragmc.lowdraglib.client.renderer.block;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RendererBlockEntity extends BlockEntity {

    @Getter @Setter
    IRenderer renderer = IRenderer.EMPTY;

    public RendererBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE(), pos, blockState);
    }

    @ExpectPlatform
    public static BlockEntityType<?> TYPE() {
        throw new AssertionError();
    }
}
