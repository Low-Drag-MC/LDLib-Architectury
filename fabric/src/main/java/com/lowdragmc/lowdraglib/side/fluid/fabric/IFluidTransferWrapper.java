package com.lowdragmc.lowdraglib.side.fluid.fabric;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * very low efficient implementation
 */
public class IFluidTransferWrapper extends SnapshotParticipant<List<ResourceAmount<FluidVariant>>> implements Storage<FluidVariant> {

    public static @NotNull IFluidTransferWrapper of(@NotNull IFluidTransfer back) {
        return new IFluidTransferWrapper(back);
    }

    private final @NotNull IFluidTransfer back;

    protected IFluidTransferWrapper(@NotNull IFluidTransfer back) {
        this.back = back;
    }

    @Override
    protected List<ResourceAmount<FluidVariant>> createSnapshot() {
        List<ResourceAmount<FluidVariant>> snapshot = new LinkedList<>();
        for (int i = 0; i < getBack().getTanks(); i++) {
            FluidStack fluidInTank = getBack().getFluidInTank(i);
            snapshot.add(new ResourceAmount<>(FluidHelperImpl.toFluidVariant(fluidInTank), fluidInTank.getAmount()));
        }
        return snapshot;
    }

    @Override
    protected void readSnapshot(@NotNull List<ResourceAmount<FluidVariant>> snapshot) {
        int i = 0;
        for (ResourceAmount<FluidVariant> ra : snapshot) {
            getBack().setFluidInTank(i, FluidHelperImpl.toFluidStack(ra.resource(), ra.amount()));
            i++;
        }
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        return getBack().fill(FluidHelperImpl.toFluidStack(resource, maxAmount), false);
    }

    @Override
    public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        return getBack().fill(FluidHelperImpl.toFluidStack(resource, maxAmount), true);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        return getBack().drain(FluidHelperImpl.toFluidStack(resource, maxAmount), false).getAmount();
    }

    @Override
    public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        return getBack().drain(FluidHelperImpl.toFluidStack(resource, maxAmount), true).getAmount();
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getBack().getTanks();
            }

            @Override
            public StorageView<FluidVariant> next() {
                return new SlotView(i++);
            }
        };
    }

    public @NotNull IFluidTransfer getBack() {
        return back;
    }

    protected class SlotView implements StorageView<FluidVariant> {

        protected final int index;

        public SlotView(int index) {
            this.index = index;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return IFluidTransferWrapper.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return getBack().getFluidInTank(index).isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            return FluidHelperImpl.toFluidVariant(getBack().getFluidInTank(index));
        }

        @Override
        public long getAmount() {
            return getBack().getFluidInTank(index).getAmount();
        }

        @Override
        public long getCapacity() {
            return getBack().getTankCapacity(index);
        }

    }

}
