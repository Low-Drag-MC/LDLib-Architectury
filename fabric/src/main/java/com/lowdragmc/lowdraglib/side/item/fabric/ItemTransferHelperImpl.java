package com.lowdragmc.lowdraglib.side.item.fabric;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote ItemTransferHelperImpl
 */
public class ItemTransferHelperImpl {

    public static Storage<ItemVariant> toItemVariantStorage(IItemTransfer itemTransfer) {
        return new Storage<>() {

            @Override
            public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                long left = maxAmount;
                for (int i = 0; i < itemTransfer.getSlots(); i++) {
                    left = itemTransfer.insertItem(i, (resource.toStack((int) left)), false).getCount();
                    if (left == 0) break;
                }
                return maxAmount - left;
            }

            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                long left = maxAmount;
                for (int i = 0; i < itemTransfer.getSlots(); i++) {
                    left -= itemTransfer.extractItem(i, (int) left, false).getCount();
                    if (left == 0) break;
                }
                return maxAmount - left;
            }

            @Override
            public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                long left = maxAmount;
                for (int i = 0; i < itemTransfer.getSlots(); i++) {
                    left = itemTransfer.insertItem(i, (resource.toStack((int) left)), true).getCount();
                    if (left == 0) break;
                }
                return maxAmount - left;
            }

            @Override
            public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                long left = maxAmount;
                for (int i = 0; i < itemTransfer.getSlots(); i++) {
                    left -= itemTransfer.extractItem(i, (int) left, true).getCount();
                    if (left == 0) break;
                }
                return maxAmount - left;
            }

            @Override
            public Iterator<StorageView<ItemVariant>> iterator() {
                List<StorageView<ItemVariant>> views = new ArrayList<>();
                for (int i = 0; i < itemTransfer.getSlots(); i++) {
                    views.add(toSingleStackStorage(itemTransfer, i));
                }
                return views.iterator();
            }
        };
    }

    public static SingleStackStorage toSingleStackStorage(IItemTransfer itemTransfer, int slot) {
        return new SingleStackStorage() {
            @Override
            protected ItemStack getStack() {
                return itemTransfer.getStackInSlot(slot);
            }

            @Override
            protected void setStack(ItemStack stack) {
                itemTransfer.setStackInSlot(slot, stack);
            }
        };
    }

    public static IItemTransfer toItemTransfer(Storage<ItemVariant> storage) {
        var iter = storage.iterator();
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        while (iter.hasNext()) {
            views.add(iter.next());
        }
        return new IItemTransfer() {
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
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.isEmpty()) return stack;
                var copied = stack.copy();
                Storage<ItemVariant> handler = storage;
                if (views.get(slot) instanceof SingleStackStorage storage) {
                    handler = storage;
                }
                try (Transaction transaction = Transaction.openOuter()) {
                    copied.shrink(simulate ?
                            (int) handler.simulateInsert(ItemVariant.of(stack), stack.getCount(), transaction) :
                            (int) handler.insert(ItemVariant.of(stack), stack.getCount(), transaction));
                    transaction.commit();
                }
                return copied;
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                var item = getStackInSlot(slot);
                if (item.isEmpty()) return ItemStack.EMPTY;
                Storage<ItemVariant> handler = storage;
                if (views.get(slot) instanceof SingleStackStorage storage) {
                    handler = storage;
                }
                var copied = item.copy();
                try (Transaction transaction = Transaction.openOuter()) {
                    copied.setCount(simulate ?
                            (int) handler.simulateExtract(ItemVariant.of(item), amount, transaction) :
                            (int) handler.extract(ItemVariant.of(item), amount, transaction));
                    transaction.commit();
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
        };
    }

    public static IItemTransfer getItemTransfer(Level level, BlockPos pos, @Nullable Direction direction) {
        var storage = ItemStorage.SIDED.find(level, pos, direction);
        return storage == null ? null : toItemTransfer(storage);
    }

    public static void exportToTarget(IItemTransfer source, int maxAmount, Predicate<ItemStack> predicate, Level level, BlockPos pos, @Nullable Direction direction) {
        var target = ItemStorage.SIDED.find(level, pos, direction);
        if (target != null) {
            StorageUtil.move(toItemVariantStorage(source), target, iv -> {
                if (predicate == null) return true;
                return predicate.test(iv.toStack());
            }, maxAmount, null);
        }
    }

    public static void importToTarget(IItemTransfer target, int maxAmount, Predicate<ItemStack> predicate, Level level, BlockPos pos, @Nullable Direction direction) {
        var source = ItemStorage.SIDED.find(level, pos, direction);
        if (source != null) {
            StorageUtil.move(source, toItemVariantStorage(target), iv -> {
                if (predicate == null) return true;
                return predicate.test(iv.toStack());
            }, maxAmount, null);
        }
    }
}
