package com.lowdragmc.lowdraglib.msic;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/25
 * @implNote ItemTransferList
 */
public class ItemTransferList implements IItemTransfer {

    @Persisted
    public final IItemTransfer[] transfers;

    public ItemTransferList(IItemTransfer... transfers) {
        this.transfers = transfers;
    }

    public ItemTransferList(List<IItemTransfer> transfers) {
        this.transfers = transfers.toArray(IItemTransfer[]::new);
    }

    @Override
    public int getSlots() {
        return Arrays.stream(transfers).mapToInt(IItemTransfer::getSlots).sum();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.getStackInSlot(slot - index);
            }
            index += transfer.getSlots();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                transfer.setStackInSlot(slot - index, stack);
            }
            index += transfer.getSlots();
        }
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.insertItem(slot - index, stack, simulate);
            }
            index += transfer.getSlots();
        }
        return stack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.extractItem(slot - index, amount, simulate);
            }
            index += transfer.getSlots();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.getSlotLimit(slot - index);
            }
            index += transfer.getSlots();
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.isItemValid(slot - index, stack);
            }
            index += transfer.getSlots();
        }
        return false;
    }
}
