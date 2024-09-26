package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycleFluidStorage implements IFluidStorage {
    private List<FluidStack> storages;
    @Getter @Setter
    private long capacity;

    public CycleFluidStorage(long capacity, List<FluidStack> storages) {
        setCapacity(capacity);
        updateStacks(storages);
    }

    public void updateStacks(List<FluidStack> storages) {
        this.storages = storages;
    }


    @NotNull
    @Override
    public FluidStack getFluid() {
        return storages == null || storages.isEmpty() ? FluidStack.empty() : storages.get(Math.abs((int)(System.currentTimeMillis() / 1000) % storages.size()));
    }

    @Override
    public void setFluid(FluidStack fluid) {
        updateStacks(List.of(fluid));
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
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
}
