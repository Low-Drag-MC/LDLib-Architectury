package com.lowdragmc.lowdraglib.client.renderer.block;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

// used to present a renderer block
public class RendererBlock extends Block implements EntityBlock, IBlockRendererProvider {

    public static final RendererBlock BLOCK = new RendererBlock();
    private static final IRenderer renderer = new RendererBlockRenderer();

    private RendererBlock() {
        super(Properties.of(Material.STONE).noOcclusion().destroyTime(2));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RendererBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public IRenderer getRenderer(BlockState state) {
        return renderer;
    }
}
