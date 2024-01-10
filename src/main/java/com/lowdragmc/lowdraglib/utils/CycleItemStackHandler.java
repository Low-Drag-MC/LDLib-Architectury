package com.lowdragmc.lowdraglib.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class CycleItemStackHandler implements IItemHandlerModifiable {
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
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
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

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }
}
