package com.lowdragmc.lowdraglib.side.fluid.fabric;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/8/15
 * @implNote FluidTransferProxyStorage
 */
@SuppressWarnings("UnstableApiUsage")
class FluidTransferProxyStorage extends SnapshotParticipant<Object> implements Storage<FluidVariant> {
    public final IFluidTransfer transfer;

    public FluidTransferProxyStorage(IFluidTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    protected void releaseSnapshot(Object snapshot) {
        super.releaseSnapshot(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        transfer.onContentsChanged();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        var filled = transfer.fill(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), true, false);
        if (filled != 0) {
            updateSnapshots(transaction);
            return transfer.fill(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), false, false);
        }
        return filled;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        var drained = transfer.drain(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), true, false).getAmount();
        if (drained != 0) {
            updateSnapshots(transaction);
            return transfer.drain(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), false, false).getAmount();
        }
        return drained;
    }

    @Override
    public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        return transfer.fill(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), true, false);
    }

    @Override
    public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        return transfer.drain(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), true, false).getAmount();
    }

    @Override
    public boolean supportsInsertion() {
        for (int i = 0; i < transfer.getTanks(); i++) {
            if (transfer.supportsFill(i)) return true;
        }
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        for (int i = 0; i < transfer.getTanks(); i++) {
            if (transfer.supportsDrain(i)) return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public Iterator<StorageView<FluidVariant>> iterator() {
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (int i = 0; i < transfer.getTanks(); i++) {
            views.add(new FluidStorageView(i));
        }
        return views.iterator();
    }

    @Override
    protected Object createSnapshot() {
        return transfer.createSnapshot();
    }

    @Override
    protected void readSnapshot(Object snapshot) {
        transfer.restoreFromSnapshot(snapshot);
    }

    private class FluidStorageView extends SnapshotParticipant<FluidStack> implements StorageView<FluidVariant> {
        private final int index;

        private FluidStorageView(int index) {
            this.index = index;
        }

        @Override
        protected void onFinalCommit() {
            transfer.onContentsChanged();
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!transfer.supportsDrain(index)) return 0;

            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            var drained = transfer.drain(index, FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), true, false).getAmount();

            if (drained != 0) {
                updateSnapshots(transaction);
                return transfer.drain(FluidStack.create(resource.getFluid(), maxAmount, resource.getNbt()), false, false).getAmount();
            }

            return 0;
        }

        @Override
        public boolean isResourceBlank() {
            return transfer.getFluidInTank(index).isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            return FluidHelperImpl.toFluidVariant(transfer.getFluidInTank(index));
        }

        @Override
        public long getAmount() {
            return transfer.getFluidInTank(index).getAmount();
        }

        @Override
        public long getCapacity() {
            return transfer.getTankCapacity(index);
        }

        @Override
        protected FluidStack createSnapshot() {
            return transfer.getFluidInTank(index).copy();
        }

        @Override
        protected void readSnapshot(FluidStack snapshot) {
            transfer.setFluidInTank(index, snapshot);
        }

    }
}
