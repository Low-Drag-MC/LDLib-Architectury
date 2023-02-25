package com.lowdragmc.lowdraglib.side.fluid.forge;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import lombok.Getter;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2023/2/23
 * @implNote FluidTransferWrapper
 */
public record FluidTransferWrapper(@Getter IFluidHandler handler) implements IFluidTransfer {

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidHelperImpl.toFluidStack(handler.getFluidInTank(tank));
    }

    @Override
    public long getTankCapacity(int tank) {
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return handler.isFluidValid(tank, FluidHelperImpl.toFluidStack(stack));
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        return handler.fill(FluidHelperImpl.toFluidStack(resource), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, boolean simulate) {
        return FluidHelperImpl.toFluidStack(handler.drain(FluidHelperImpl.toFluidStack(resource), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE));
    }
}
