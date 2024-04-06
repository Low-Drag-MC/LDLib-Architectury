package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TagOrCycleItemStackTransfer implements IItemTransfer {
    @Getter
    private List<Either<Pair<TagKey<Item>, Integer>, List<ItemStack>>> stacks;

    private List<List<ItemStack>> unwrapped = null;


    public TagOrCycleItemStackTransfer(List<Either<Pair<TagKey<Item>, Integer>, List<ItemStack>>> stacks) {
        updateStacks(stacks);
    }

    public void updateStacks(List<Either<Pair<TagKey<Item>, Integer>, List<ItemStack>>> stacks) {
        this.stacks = stacks;
        this.unwrapped = null;
    }

    public List<List<ItemStack>> getUnwrapped() {
        if (unwrapped == null) {
            unwrapped = stacks.stream()
                .map(
                    tagOrItem -> tagOrItem.map(
                        tag -> BuiltInRegistries.ITEM.getTag(tag.getFirst())
                            .map(holderSet -> holderSet.stream()
                                .map(holder -> new ItemStack(holder.value(), tag.getSecond()))
                                .collect(Collectors.toList()))
                            .orElseGet(ArrayList::new),
                        Function.identity()))
                .collect(Collectors.toList());
        }
        return unwrapped;
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        List<ItemStack> stackList = getUnwrapped().get(slot);
        return stackList == null || stackList.isEmpty() ? ItemStack.EMPTY : stackList.get(Math.abs((int)(System.currentTimeMillis() / 1000) % stackList.size()));
    }

    @Override
    public void setStackInSlot(int index, ItemStack stack) {
        if (index >= 0 && index < stacks.size()) {
            stacks.set(index, Either.right(List.of(stack)));
            unwrapped = null;
        }
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
