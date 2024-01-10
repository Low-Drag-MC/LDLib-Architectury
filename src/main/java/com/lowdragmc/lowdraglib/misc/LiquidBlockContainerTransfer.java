package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote LiquidBlockContainerTransfer
 */
public class LiquidBlockContainerTransfer implements IFluidHandler {
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
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        // NOTE: "Filling" means placement in this context!
        if (resource.getAmount() >= FluidHelper.getBucket()) {
            BlockState state = world.getBlockState(blockPos);
            if (liquidContainer.canPlaceLiquid(null, world, blockPos, state, resource.getFluid())) {
                //If we are executing try to actually fill the container, if it failed return that we failed
                if (action.simulate() || liquidContainer.placeLiquid(world, blockPos, state, resource.getFluid().defaultFluidState())) {
                    return FluidHelper.getBucket();
                }
            }
        }
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    public static class BlockWrapper implements IFluidHandler {

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
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            // NOTE: "Filling" means placement in this context!
            if (resource.getAmount() < FluidHelper.getBucket()) {
                return 0;
            }
            if (action.execute()) {
                FluidTransferHelper.destroyBlockOnFluidPlacement(world, blockPos);
                world.setBlock(blockPos, state, Block.UPDATE_ALL_IMMEDIATE);
            }
            return FluidHelper.getBucket();
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}
