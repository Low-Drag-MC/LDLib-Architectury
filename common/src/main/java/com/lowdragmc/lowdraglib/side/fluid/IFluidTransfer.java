package com.lowdragmc.lowdraglib.side.fluid;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote IFluidTransfer copied form forge
 */
public interface IFluidTransfer {
    IFluidTransfer EMPTY = new IFluidTransfer() {
        @Override
        public int getTanks() {
            return 0;
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.empty();
        }

        @Override
        public long getTankCapacity(int tank) {
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return false;
        }

        @Override
        public long fill(FluidStack resource, boolean simulate) {
            return 0;
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, boolean simulate) {
            return FluidStack.empty();
        }
    };

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    int getTanks();

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @Nonnull
    FluidStack getFluidInTank(int tank);

    default void setFluidInTank(int tank, @Nonnull FluidStack fluidStack) {
        throw new RuntimeException("FluidTransfer %s doesn't support set fluid in tank".formatted(this));
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return     The maximum fluid amount held by the tank.
     */
    long getTankCapacity(int tank);

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    boolean isFluidValid(int tank, @Nonnull FluidStack stack);

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidTransfer.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    long fill(FluidStack resource, boolean simulate);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidTransfer.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    FluidStack drain(FluidStack resource, boolean simulate);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidTransfer.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    default FluidStack drain(long maxDrain, boolean simulate) {
        if (maxDrain == 0) {
            return FluidStack.empty();
        }
//        if (!simulate) markAsDirty();
        FluidStack totalDrained = null;
        for (int i = 0; i < getTanks(); i++) {
            FluidStack handler = getFluidInTank(i);
            if (handler.isEmpty()) continue;
            if (totalDrained == null) { // found
                totalDrained = handler.copy();
                totalDrained.setAmount(Math.min(maxDrain, totalDrained.getAmount()));
                maxDrain -= totalDrained.getAmount();
                if (!simulate) {
                    handler.shrink(totalDrained.getAmount());
                    onContentsChanged();
                }
            } else if (totalDrained.isFluidEqual(handler)){
                var toDrain = Math.min(maxDrain, handler.getAmount());
                maxDrain -= toDrain;
                totalDrained.grow(toDrain);
                if (!simulate) {
                    handler.shrink(toDrain);
                    onContentsChanged();
                }
            }
            if (maxDrain <= 0) break;
        }
        return totalDrained == null ? FluidStack.empty() : totalDrained;
    }

    default void onContentsChanged() {

    }
}
