package com.lowdragmc.lowdraglib.side.fluid;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote IFluidStorage
 */
public interface IFluidStorage extends IFluidTransfer {

    @Nonnull
    FluidStack getFluid();

    void setFluid(FluidStack fluid);

    /**
     * @return Capacity of this fluid tank.
     */
    long getCapacity();

    /**
     * @param stack Fluidstack holding the Fluid to be queried.
     * @return If the tank can hold the fluid (EVER, not at the time of query).
     */
    boolean isFluidValid(FluidStack stack);

    /**
     * @return Current amount of fluid in the tank.
     */
    default long getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    default int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    default FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    default long getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

}
