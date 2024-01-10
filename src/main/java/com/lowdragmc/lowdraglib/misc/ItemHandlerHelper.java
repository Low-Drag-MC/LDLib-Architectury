package com.lowdragmc.lowdraglib.misc;

import net.minecraft.world.item.ItemStack;

/**
 * @author KilaBash
 * @date 2023/3/13
 * @implNote ItemHandlerHelper
 */
public class ItemHandlerHelper {
    public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
        if (!first.isEmpty() && ItemStack.isSameItem(first, second) && first.hasTag() == second.hasTag()) {
            return !first.hasTag() || first.getTag().equals(second.getTag());
        } else {
            return false;
        }
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int size) {
        if (size == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(size);
            return copy;
        }
    }
}
