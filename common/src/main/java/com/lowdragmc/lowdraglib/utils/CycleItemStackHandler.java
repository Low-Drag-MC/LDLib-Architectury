package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class CycleItemStackHandler implements IItemTransfer {
    private List<List<ItemStack>> stacks;


    public CycleItemStackHandler(List<List<ItemStack>> stacks) {
        updateStacks(stacks);
    }

    public void updateStacks(List<List<ItemStack>> stacks) {
        this.stacks = stacks;
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int i) {
        List<ItemStack> stackList = stacks.get(i);
        return stackList == null || stackList.isEmpty() ? ItemStack.EMPTY : stackList.get(Math.abs((int)(System.currentTimeMillis() / 1000) % stackList.size()));
    }

    @Override
    public void setStackInSlot(int index, ItemStack stack) {
        if (index >= 0 && index < stacks.size()) {
            stacks.set(index, List.of(stack));
        }
    }

    public List<ItemStack> getStackList(int i){
        return stacks.get(i);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int i, @Nonnull ItemStack itemStack, boolean b, boolean notifyChanges) {
        return itemStack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int i, int i1, boolean b, boolean notifyChanges) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
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
