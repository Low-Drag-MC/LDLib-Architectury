package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote LiquidBlockContainerTransfer
 */
public class LiquidBlockContainerTransfer implements IFluidTransfer {
    protected final LiquidBlockContainer liquidContainer;
    protected final Level world;
    protected final BlockPos blockPos;

    public LiquidBlockContainerTransfer(LiquidBlockContainer liquidContainer, Level world, BlockPos blockPos) {
        this.liquidContainer = liquidContainer;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.empty();
    }

    @Override
    public long getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, boolean simulate) {
        return FluidStack.empty();
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        // NOTE: "Filling" means placement in this context!
        if (resource.getAmount() >= FluidHelper.getBucket()) {
            BlockState state = world.getBlockState(blockPos);
            if (liquidContainer.canPlaceLiquid(world, blockPos, state, resource.getFluid())) {
                //If we are executing try to actually fill the container, if it failed return that we failed
                if (simulate || liquidContainer.placeLiquid(world, blockPos, state, resource.getFluid().defaultFluidState())) {
                    return FluidHelper.getBucket();
                }
            }
        }
        return 0;
    }

    public static class BlockWrapper implements IFluidTransfer {

        protected final BlockState state;
        protected final Level world;
        protected final BlockPos blockPos;

        public BlockWrapper(BlockState state, Level world, BlockPos blockPos) {
            this.state = state;
            this.world = world;
            this.blockPos = blockPos;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.empty();
        }


        @Override
        public long getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return true;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, boolean simulate) {
            return FluidStack.empty();
        }

        @Override
        public long fill(FluidStack resource, boolean simulate) {
            // NOTE: "Filling" means placement in this context!
            if (resource.getAmount() < FluidHelper.getBucket()) {
                return 0;
            }
            if (!simulate) {
                FluidTransferHelper.destroyBlockOnFluidPlacement(world, blockPos);
                world.setBlock(blockPos, state, Block.UPDATE_ALL_IMMEDIATE);
            }
            return FluidHelper.getBucket();
        }
    }
}
