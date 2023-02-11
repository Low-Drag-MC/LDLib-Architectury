package com.lowdragmc.lowdraglib.side.item.fabric;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote ItemTransferHelperImpl
 */
public class ItemTransferHelperImpl {
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
}
