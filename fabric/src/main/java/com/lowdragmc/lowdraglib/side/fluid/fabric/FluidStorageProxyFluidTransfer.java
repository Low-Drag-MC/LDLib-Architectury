package com.lowdragmc.lowdraglib.side.fluid.fabric;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/8/15
 * @implNote FluidStorageProxyFluidTransfer
 */
@SuppressWarnings("UnstableApiUsage")
class FluidStorageProxyFluidTransfer implements IFluidTransfer {

    private final List<StorageView<FluidVariant>> views;
    private final Storage<FluidVariant> storage;

    public FluidStorageProxyFluidTransfer(Storage<FluidVariant> storage) {
        var iter = storage.iterator();
        views = new ArrayList<>();
        while (iter.hasNext()) {
            views.add(iter.next());
        }
        this.storage = storage;
    }

    @Override
    public int getTanks() {
        return views.size();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidHelperImpl.toFluidStack(views.get(tank));
    }

    @Override
    public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {
        throw new NotImplementedException("Try to set fluid of the proxy transfer in a specific tank.");
    }

    @Override
    public long getTankCapacity(int tank) {
        return views.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        boolean result;
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            result = storage.simulateExtract(FluidHelperImpl.toFluidVariant(stack), stack.getAmount(), transaction) > 0;
        }
        return result;
    }

    @Override
    public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        throw new NotImplementedException("Try to fill the proxy transfer with a specific tank.");
    }

    @NotNull
    @Override
    public FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (tank >= 0 && tank < views.size()) {
            long drained;
            var storage = views.get(tank);
            try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
                drained = storage.extract(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction);
                if (!simulate && drained > 0) {
                    transaction.commit();
                }
            }
            return resource.copy(drained);
        }
        return FluidStack.empty();
    }

    @Override
    public long fill(FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (resource.isEmpty()) return 0;
        long filled;
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            filled = storage.insert(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction);
            if (!simulate && filled > 0) {
                transaction.commit();
            }
        }
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, boolean simulate, boolean notifyChanges) {
        if (resource.isEmpty()) return FluidStack.empty();
        var copied = resource.copy();
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            var extracted = storage.extract(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction);
            copied.setAmount(extracted);
            if (!simulate && extracted > 0) {
                transaction.commit();
            }
        }
        return copied;
    }

    @Override
    public boolean supportsFill(int tank) {
        return storage.supportsInsertion();
    }

    @Override
    public boolean supportsDrain(int tank) {
        return storage.supportsExtraction();
    }

    @NotNull
    @Override
    public Object createSnapshot() {
        throw new NotImplementedException("Try to create a snapshot for a proxy transfer.");
    }

    @Override
    public void restoreFromSnapshot(Object snapshot) {
        throw new NotImplementedException("Try to restore a snapshot for a proxy transfer.");
    }
}
