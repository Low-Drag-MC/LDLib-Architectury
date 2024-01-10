package com.lowdragmc.lowdraglib.side.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote ItemTransferHelper
 */
public class ItemTransferHelper {
    public static IItemHandler getItemTransfer(Level level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return Capabilities.ItemHandler.BLOCK.getCapability(level, pos, blockEntity.getBlockState(), blockEntity, direction);
            }
        }
        return null;
    }

    public static void exportToTarget(IItemHandler source, int maxAmount, Predicate<ItemStack> predicate, Level level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction);
                if (cap != null) {
                    for (int srcIndex = 0; srcIndex < source.getSlots(); srcIndex++) {
                        ItemStack sourceStack = source.extractItem(srcIndex, Integer.MAX_VALUE, true);
                        if (sourceStack.isEmpty() || !predicate.test(sourceStack)) {
                            continue;
                        }
                        ItemStack remainder = insertItem(cap, sourceStack, true);
                        int amountToInsert = sourceStack.getCount() - remainder.getCount();
                        if (amountToInsert > 0) {
                            sourceStack = source.extractItem(srcIndex, Math.min(maxAmount, amountToInsert), false);
                            insertItem(cap, sourceStack, false);
                            maxAmount -= Math.min(maxAmount, amountToInsert);
                            if (maxAmount <= 0) return;
                        }
                    }
                }
            }
        }
    }

    public static void importToTarget(IItemHandler target, int maxAmount, Predicate<ItemStack> predicate, Level level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction);
                if (cap != null) {
                    for (int srcIndex = 0; srcIndex < cap.getSlots(); srcIndex++) {
                        ItemStack sourceStack = cap.extractItem(srcIndex, Integer.MAX_VALUE, true);
                        if (sourceStack.isEmpty() || !predicate.test(sourceStack)) {
                            continue;
                        }
                        ItemStack remainder = insertItem(target, sourceStack, true);
                        int amountToInsert = sourceStack.getCount() - remainder.getCount();
                        if (amountToInsert > 0) {
                            sourceStack = cap.extractItem(srcIndex, Math.min(maxAmount, amountToInsert), false);
                            insertItem(target, sourceStack, false);
                            maxAmount -= Math.min(maxAmount, amountToInsert);
                        }
                        if (maxAmount <= 0) return;
                    }
                }
            }
        }
    }

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

    @Nonnull
    public static ItemStack insertItem(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    /**
     * Inserts the ItemStack into the inventory, filling up already present stacks first.
     * This is equivalent to the behaviour of a player picking up an item.
     * Note: This function stacks items without subtypes with different metadata together.
     */
    @Nonnull
    public static ItemStack insertItemStacked(IItemHandler inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (canItemStacksStackRelaxed(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }


    /**
     * A relaxed version of canItemStacksStack that stacks itemstacks with different metadata if they don't have subtypes.
     * This usually only applies when players pick up items.
     */
    public static boolean canItemStacksStackRelaxed(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;

        if (!a.isStackable())
            return false;

        if (a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }

    public static void giveItemToPlayer(Player player, ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }


    /**
     * Inserts the given itemstack into the players inventory.
     * If the inventory can't hold it, the item will be dropped in the world at the players position.
     *
     * @param player The player to give the item to
     * @param stack  The itemstack to insert
     */
    public static void giveItemToPlayer(Player player, @Nonnull ItemStack stack, int preferredSlot) {
        if (stack.isEmpty()) return;

        IItemHandler inventory = new InvWrapper(player.getInventory());
        Level level = player.level();

        // try adding it into the inventory
        ItemStack remainder = stack;
        // insert into preferred slot first
        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }
        // then into the inventory in general
        if (!remainder.isEmpty()) {
            remainder = insertItemStacked(inventory, remainder, false);
        }

        // play sound if something got picked up
        if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        // drop remaining itemstack into the level
        if (!remainder.isEmpty() && !level.isClientSide) {
            ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

            level.addFreshEntity(entityitem);
        }
    }
}
