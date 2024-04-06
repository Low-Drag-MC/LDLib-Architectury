package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import lombok.Getter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagItemStackTransfer implements IItemTransfer {
    @Getter
    private TagKey<Item> tag;
    @Getter
    private int stackSize;
    private List<ItemStack> resolvedItems;

    public TagItemStackTransfer(TagKey<Item> tag, int stackSize) {
        this.tag = tag;
        this.stackSize = stackSize;
    }

    public void setTag(TagKey<Item> tag) {
        this.tag = tag;
        this.resolvedItems = null;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
        resolvedItems = null;
    }

    public List<ItemStack> getResolvedItems() {
        if (resolvedItems == null) {
            HolderSet.Named<Item> parsed = BuiltInRegistries.ITEM.getTag(tag).orElse(null);
            if (parsed != null) {
                this.resolvedItems = parsed.stream().map(holder -> new ItemStack(holder.value(), stackSize)).collect(Collectors.toList());
            } else {
                this.resolvedItems = new ArrayList<>();
            }
        }
        return resolvedItems;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return getResolvedItems().get(Math.abs((int)(System.currentTimeMillis() / 1000) % resolvedItems.size()));
    }

    @Override
    public void setStackInSlot(int index, ItemStack stack) {
        if (resolvedItems == null) {
            resolvedItems = new ArrayList<>();
        } else {
            resolvedItems.clear();
        }
        resolvedItems.add(stack);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean notifyChanges) {
        return stack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
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
