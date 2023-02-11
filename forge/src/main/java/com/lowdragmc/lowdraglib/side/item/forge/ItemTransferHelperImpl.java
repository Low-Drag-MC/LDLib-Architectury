package com.lowdragmc.lowdraglib.side.item.forge;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote ItemTransferHelperImpl
 */
public class ItemTransferHelperImpl {
    public static IItemHandler toItemHandler(IItemTransfer itemTransfer) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return itemTransfer.getSlots();
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return itemTransfer.getStackInSlot(slot);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return itemTransfer.insertItem(slot, stack, simulate);
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return itemTransfer.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return itemTransfer.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return itemTransfer.isItemValid(slot, stack);
            }
        };
    }

    public static IItemTransfer toItemTransfer(IItemHandler handler) {
        return new IItemTransfer() {
            @Override
            public int getSlots() {
                return handler.getSlots();
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return handler.getStackInSlot(slot);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return handler.insertItem(slot, stack, simulate);
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return handler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return handler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return handler.isItemValid(slot, stack);
            }
        };
    }

    public static IItemTransfer getItemTransfer(Level level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                var cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).resolve();
                if (cap.isPresent()) {
                    return toItemTransfer(cap.get());
                }
            }
        }
        return null;
    }
}
