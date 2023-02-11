package com.lowdragmc.lowdraglib.utils;

import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public final class ItemStackKey {

    private final ItemStack[] itemStack;
    private final int hashCode;

    public ItemStackKey(ItemStack... itemStack) {
        this.itemStack = Arrays.stream(itemStack).map(item -> {
            ItemStack copied = item.copy();
            copied.setCount(1);
            return copied;
        }).toArray(ItemStack[]::new);
        this.hashCode = makeHashCode();
    }

    public ItemStack[] getItemStack() {
        return itemStack;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemStackKey that)) return false;
        if (that.itemStack.length == this.itemStack.length) {
            for (ItemStack a : that.itemStack) {
                if (Arrays.stream(this.itemStack).noneMatch(b -> ItemStack.matches(a, b))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int makeHashCode() {
        int itemsHash = 0;
        for (ItemStack stack : itemStack) {
            itemsHash += stack.getItem().hashCode();
            itemsHash += stack.getDamageValue();
            itemsHash += stack.getTag() == null ? 0 : stack.getTag().hashCode();
        }
        return itemsHash;
    }

    @Override
    public String toString() {
        return Arrays.toString(itemStack);
    }
}
