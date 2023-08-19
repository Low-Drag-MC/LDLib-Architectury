package com.lowdragmc.lowdraglib.side.fluid;

import org.jetbrains.annotations.ApiStatus;
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
        public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {

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
        public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
            return 0;
        }

        @Override
        public boolean supportsFill(int tank) {
            return false;
        }

        @NotNull
        @Override
        public FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
            return FluidStack.empty();
        }

        @Override
        public boolean supportsDrain(int tank) {
            return false;
        }

        @NotNull
        @Override
        public Object createSnapshot() {
            return new Object();
        }

        @Override
        public void restoreFromSnapshot(Object snapshot) {

        }
    };

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    int getTanks();

    /**
     * Returns the FluidStack in a given tank. without notify changes.
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

    /**
     * Set the FluidStack of specific tank without notify changes.
     * <br/>
     * You have to implement it but do not call it yourself, unless you can make sure it works.
     */
    @ApiStatus.Internal
    void setFluidInTank(int tank, @Nonnull FluidStack fluidStack);

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
     * <br/>
     * You have to implement it but do not call it yourself, you should always use {@link IFluidTransfer#fill(FluidStack, boolean, boolean)} instead.
     *
     * @param tank tank index..
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate   If SIMULATE, fill will only be simulated.
     * @param notifyChanges should notify changes if simulate is false and it does accept fluid.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @ApiStatus.Internal
    long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges);

    /**
     * Determines whether the specified tank can be inserted into.
     *
     * <p>
     * This does NOT check whether a specific amount or type of fluid can be inserted, just whether the tank
     * supports insertion at all.
     * </p>
     *
     * @param tank Tank to query for insertion
     * @return Whether fluid can be filled into the specified tank.
     */
    boolean supportsFill(int tank);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidTransfer.
     * <br/>
     * You have to implement it but do not call it yourself, you should always use {@link IFluidTransfer#drain(FluidStack, boolean, boolean)} )} instead.
     *
     * @param tank tank index..
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @param notifyChanges should notify changes if simulate is false, and it does be drained fluid.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @ApiStatus.Internal
    FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges);

    /**
     * Determines whether the specified tank can be extracted from.
     *
     * <p>
     * This does NOT check whether a specific amount or type of fluid can be extracted, just whether the tank
     * supports extraction at all.
     * </p>
     *
     * @param tank Tank to query for extraction
     * @return Whether fluid can be drained from the specified tank.
     */
    boolean supportsDrain(int tank);

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidTransfer.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate   If SIMULATE, fill will only be simulated.
     * @param notifyChanges should notify changes if simulate is false and it does accept fluid.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    default long fill(FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (resource.isEmpty()) return 0;
        long filled = 0;
        for (int i = 0; i < getTanks(); i++) {
            filled += fill(i, resource.copy(resource.getAmount() - filled), simulate, false);
            if (filled == resource.getAmount()) break;
        }
        if (notifyChanges && filled > 0 && !simulate) {
            onContentsChanged();
        }
        return filled;
    }

    default long fill(FluidStack resource, boolean simulate) {
        return fill(resource, simulate, !simulate);
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidTransfer.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @param notifyChanges should notify changes if simulate is false, and it does be drained fluid.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    default FluidStack drain(FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (resource.isEmpty()) return FluidStack.empty();
        long drained = 0;
        for (int i = 0; i < getTanks(); i++) {
            drained += drain(i, resource.copy(resource.getAmount() - drained), simulate, false).getAmount();
            if (drained == resource.getAmount()) break;
        }
        if (notifyChanges && drained > 0 && !simulate) {
            onContentsChanged();
        }
        return resource.copy(drained);
    }

    default FluidStack drain(FluidStack resource, boolean simulate) {
        return drain(resource, simulate, !simulate);
    }

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
    default FluidStack drain(long maxDrain, boolean simulate, boolean notifyChanges) {
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
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                }
            } else if (totalDrained.isFluidEqual(handler)){
                var toDrain = Math.min(maxDrain, handler.getAmount());
                maxDrain -= toDrain;
                totalDrained.grow(toDrain);
                if (!simulate) {
                    handler.shrink(toDrain);
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                }
            }
            if (maxDrain <= 0) break;
        }
        return totalDrained == null ? FluidStack.empty() : totalDrained;
    }

    default FluidStack drain(long maxDrain, boolean simulate) {
        return drain(maxDrain, simulate, !simulate);
    }

    default void onContentsChanged() {

    }

    /**
     * snapshot for fabric. non null
     * <br/>
     * Do not call it yourself, unless you can make sure it works.
     */
    @Nonnull
    @ApiStatus.Internal
    Object createSnapshot();

    /**
     * <br/>
     * Do not call it yourself, unless you can make sure it works.
     */
    @ApiStatus.Internal
    void restoreFromSnapshot(Object snapshot);

}
