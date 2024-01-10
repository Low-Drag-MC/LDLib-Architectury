package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/05/24
 * @implNote TestBlock
 */
public class TestBlock extends Block implements EntityBlock, IBlockRendererProvider {

    public static final TestBlock BLOCK = new TestBlock();
    private TestBlock() {
        super(Properties.of().noOcclusion().destroyTime(2));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TestBlockEntity(pPos, pState);
    }

    IRenderer renderer = new IModelRenderer(LDLib.location("block/cube")) {
        @Override
        public boolean reBakeCustomQuads() {
            return true;
        }
    };

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof TestBlockEntity blockEntity) {
            blockEntity.use(pPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public IRenderer getRenderer(BlockState state) {
        return renderer;
    }
}
