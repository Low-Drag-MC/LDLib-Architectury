package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidBlockTransfer
 */
public class FluidBlockTransfer implements IFluidTransfer {
    protected final LiquidBlock fluidBlock;
    protected final BlockState blockState;
    protected final Level world;
    protected final BlockPos blockPos;

    public FluidBlockTransfer(LiquidBlock fluidBlock, Level world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
        this.blockState = world.getBlockState(blockPos);
    }

    public Fluid getFluid() {
        return fluidBlock.getFluidState(blockState).getType();
    }

    public int getTanks() {
        return 1;
    }

    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank == 0 ? FluidStack.create(getFluid(), FluidHelper.getBucket()) : FluidStack.empty();
    }

    public long getTankCapacity(int tank) {
        return FluidHelper.getBucket();
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == getFluid();
    }

    public long fill(FluidStack resource, boolean simulate) {
        return 0;
    }

    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!resource.isEmpty() && resource.getFluid() == getFluid() && resource.getAmount() >= getTankCapacity(0)) {
            FluidStack drained = getFluidInTank(0).copy();
            if (!simulate) {
                world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            }
            return drained;
        }

        return FluidStack.empty();
    }

    @Override
    public boolean supportsFill(int tank) {
        return false;
    }

    @Override
    public boolean supportsDrain(int tank) {
        return true;
    }
}
