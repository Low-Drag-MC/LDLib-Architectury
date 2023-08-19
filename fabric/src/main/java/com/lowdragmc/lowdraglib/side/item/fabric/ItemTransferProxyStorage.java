package com.lowdragmc.lowdraglib.side.item.fabric;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/8/17
 * @implNote ItemTransferProxyStorage
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemTransferProxyStorage extends SnapshotParticipant<Object> implements Storage<ItemVariant> {
    final IItemTransfer transfer;

    public ItemTransferProxyStorage(IItemTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    protected void onFinalCommit() {
        transfer.onContentsChanged();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (simulateInsert(resource, maxAmount, transaction) > 0) {
            updateSnapshots(transaction);
            var left = maxAmount;
            for (int i = 0; i < transfer.getSlots(); i++) {
                left = transfer.insertItem(i, (resource.toStack((int) left)), false, false).getCount();
                if (left == 0) break;
            }
            return maxAmount - left;
        }
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (simulateExtract(resource, maxAmount, transaction) > 0) {
            updateSnapshots(transaction);
            var left = maxAmount;
            for (int i = 0; i < transfer.getSlots(); i++) {
                var extracted = transfer.extractItem(i, (int) left, false, false);
                if (resource.matches(extracted)) {
                    left -= extracted.getCount();
                    if (left == 0) break;
                }
            }
            return maxAmount - left;
        }
        return 0;
    }

    @Override
    public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        long left = maxAmount;
        for (int i = 0; i < transfer.getSlots(); i++) {
            left = transfer.insertItem(i, (resource.toStack((int) left)), true, false).getCount();
            if (left == 0) break;
        }
        return maxAmount - left;
    }

    @Override
    public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        long left = maxAmount;
        for (int i = 0; i < transfer.getSlots(); i++) {
            var extracted = transfer.extractItem(i, (int) left, true, false);
            if (resource.matches(extracted)) {
                left -= extracted.getCount();
                if (left == 0) break;
            }
        }
        return maxAmount - left;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (int i = 0; i < transfer.getSlots(); i++) {
            views.add(new ItemStorageView(i));
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

    private class ItemStorageView extends SnapshotParticipant<ItemStack> implements StorageView<ItemVariant> {
        private final int index;

        private ItemStorageView(int index) {
            this.index = index;
        }

        @Override
        protected void onFinalCommit() {
            transfer.onContentsChanged();
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            var extracted = transfer.extractItem(index, (int) maxAmount, true, false);

            if (resource.matches(extracted) && extracted.getCount() > 0) {
                updateSnapshots(transaction);
                return transfer.extractItem(index, extracted.getCount(), false, false).getCount();
            }

            return 0;
        }

        @Override
        public boolean isResourceBlank() {
            return transfer.getStackInSlot(index).isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(transfer.getStackInSlot(index));
        }

        @Override
        public long getAmount() {
            return transfer.getStackInSlot(index).getCount();
        }

        @Override
        public long getCapacity() {
            return transfer.getSlotLimit(index);
        }

        @Override
        protected ItemStack createSnapshot() {
            return transfer.getStackInSlot(index).copy();
        }

        @Override
        protected void readSnapshot(ItemStack snapshot) {
            transfer.setStackInSlot(index, snapshot);
        }
    }

}
