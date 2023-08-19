package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote BucketPickupTransfer
 */
public class BucketPickupTransfer implements IFluidTransfer {
    protected final BucketPickup bucketPickupHandler;
    protected final Level world;
    protected final BlockPos blockPos;

    public BucketPickupTransfer(BucketPickup bucketPickupHandler, Level world, BlockPos blockPos) {
        this.bucketPickupHandler = bucketPickupHandler;
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
        if (tank == 0) {
            //Best guess at stored fluid
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty()) {
                return FluidStack.create(fluidState.getType(), FluidHelper.getBucket());
            }
        }
        return FluidStack.empty();
    }

    @Override
    public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {

    }

    @Override
    public long getTankCapacity(int tank) {
        return FluidHelper.getBucket();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (!resource.isEmpty() && FluidHelper.getBucket() <= resource.getAmount()) {
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType()) {
                if (!simulate) {
                    ItemStack itemStack = bucketPickupHandler.pickupBlock(world, blockPos, world.getBlockState(blockPos));
                    if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof BucketItemAccessor bucket) {
                        FluidStack extracted = FluidStack.create(bucket.fabric_getFluid(), FluidHelper.getBucket());
                        if (!resource.isFluidEqual(extracted)) {
                            return FluidStack.empty();
                        }
                        return extracted;
                    }
                } else {
                    FluidStack extracted = FluidStack.create(fluidState.getType(), FluidHelper.getBucket());
                    if (resource.isFluidEqual(extracted)) {
                        //Validate NBT matches
                        return extracted;
                    }
                }
            }
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

    @NotNull
    @Override
    public Object createSnapshot() {
        return new Object();
    }

    @Override
    public void restoreFromSnapshot(Object snapshot) {

    }
}
