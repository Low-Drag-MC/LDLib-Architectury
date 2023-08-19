package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote InventoryItemTransfer
 */
public class ContainerTransfer implements IItemTransfer {
    @Getter
    private final Container inv;

    public ContainerTransfer(Container inv) {
        this.inv = inv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ContainerTransfer that = (ContainerTransfer) o;

        return getInv().equals(that.getInv());

    }

    @Override
    public int hashCode() {
        return getInv().hashCode();
    }

    @Override
    public int getSlots() {
        return getInv().getContainerSize();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return getInv().getItem(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean notifyChanges) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = getInv().getItem(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!getInv().canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    getInv().setItem(slot, copy);
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                    getInv().setChanged();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    getInv().setItem(slot, copy);
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                    getInv().setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!getInv().canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    getInv().setItem(slot, stack.split(m));
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                    getInv().setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    getInv().setItem(slot, stack);
                    if (notifyChanges) {
                        onContentsChanged();
                    }
                    getInv().setChanged();
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = getInv().getItem(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = getInv().removeItem(slot, m);
            if (notifyChanges) {
                onContentsChanged();
            }
            getInv().setChanged();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        getInv().setItem(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInv().getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return getInv().canPlaceItem(slot, stack);
    }

    @NotNull
    @Override
    public Object createSnapshot() {
        ItemStack[] copied = new ItemStack[inv.getContainerSize()];
        for (int i = 0; i < inv.getContainerSize(); i++) {
            copied[i] = inv.getItem(i).copy();
        }
        return copied;
    }

    @Override
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof ItemStack[] copied && copied.length == inv.getContainerSize()) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                inv.setItem(i, copied[i]);
            }
        }
    }
}
