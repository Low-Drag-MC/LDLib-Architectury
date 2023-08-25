package com.lowdragmc.lowdraglib.side.item.fabric;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/8/17
 * @implNote ItemStorageProxyItemTransfer
 */
@SuppressWarnings("UnstableApiUsage")
class ItemStorageProxyItemTransfer implements IItemTransfer {

    private final List<StorageView<ItemVariant>> views;
    private final Storage<ItemVariant> storage;

    public ItemStorageProxyItemTransfer(Storage<ItemVariant> storage) {
        var iter = storage.iterator();
        views = new ArrayList<>();
        while (iter.hasNext()) {
            views.add(iter.next());
        }
        this.storage = storage;
    }

    @Override
    public int getSlots() {
        return views.size();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return views.get(slot).getResource().toStack((int) views.get(slot).getAmount());
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean notifyChanges) {
        if (stack.isEmpty()) return stack;
        var copied = stack.copy();
        Storage<ItemVariant> handler = storage;
        if (views.get(slot) instanceof SingleStackStorage storage) {
            handler = storage;
        } else if (slot != 0) {
            return stack;
        }
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            var filled = (int) handler.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            copied.shrink(filled);
            if (!simulate && filled > 0) {
                transaction.commit();
            }
        }
        return copied;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        var item = getStackInSlot(slot);
        if (item.isEmpty()) return ItemStack.EMPTY;
        var copied = item.copy();
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            var extracted = (int) views.get(slot).extract(ItemVariant.of(item), amount, transaction);
            copied.setCount(extracted);
            if (!simulate && extracted > 0) {
                transaction.commit();
            }
        }
        return copied;
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) views.get(slot).getCapacity();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot < views.size();
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
