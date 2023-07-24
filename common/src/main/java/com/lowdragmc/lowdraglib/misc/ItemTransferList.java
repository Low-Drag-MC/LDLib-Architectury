package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2023/2/25
 * @implNote ItemTransferList
 */
public class ItemTransferList implements IItemTransfer, ITagSerializable<CompoundTag> {

    public final IItemTransfer[] transfers;
    @Setter
    protected Predicate<ItemStack> filter = item -> true;

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
                return;
            }
            index += transfer.getSlots();
        }
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!filter.test(stack)) {
            return stack;
        }
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
        if (!filter.test(stack)) {
            return false;
        }
        int index = 0;
        for (var transfer : transfers) {
            if (slot - index < transfer.getSlots()) {
                return transfer.isItemValid(slot - index, stack);
            }
            index += transfer.getSlots();
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        var list = new ListTag();
        for (var transfer : transfers) {
            if (transfer instanceof ITagSerializable<?> serializable) {
                list.add(serializable.serializeNBT());
            } else {
                LDLib.LOGGER.warn("[ItemTransferList] internal container doesn't support serialization");
            }
        }
        tag.put("slots", list);
        tag.putByte("type", list.getElementType());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var list = nbt.getList("slots", nbt.getByte("type"));
        for (int i = 0; i < list.size(); i++) {
            if (transfers[i] instanceof ITagSerializable serializable) {
                serializable.deserializeNBT(list.get(i));
            } else {
                LDLib.LOGGER.warn("[ItemTransferList] internal container doesn't support serialization");
            }
        }
    }
}
