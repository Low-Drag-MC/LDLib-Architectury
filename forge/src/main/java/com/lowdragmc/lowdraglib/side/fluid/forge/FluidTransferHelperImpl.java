package com.lowdragmc.lowdraglib.side.fluid.forge;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidTransferHelper
 */
public class FluidTransferHelperImpl {

    public static IFluidTransfer toFluidTransfer(IFluidHandler handler) {
        if (handler instanceof IFluidTransfer fluidTransfer) {
            return fluidTransfer;
        } else {
            return new IFluidTransfer() {
                @Override
                public int getTanks() {
                    return handler.getTanks();
                }

                @NotNull
                @Override
                public FluidStack getFluidInTank(int tank) {
                    return FluidHelperImpl.toFluidStack(handler.getFluidInTank(tank));
                }

                @Override
                public long getTankCapacity(int tank) {
                    return handler.getTankCapacity(tank);
                }

                @Override
                public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                    return handler.isFluidValid(tank, FluidHelperImpl.toFluidStack(stack));
                }

                @Override
                public long fill(FluidStack resource, boolean simulate) {
                    return handler.fill(FluidHelperImpl.toFluidStack(resource), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                }

                @NotNull
                @Override
                public FluidStack drain(FluidStack resource, boolean simulate) {
                    return FluidHelperImpl.toFluidStack(handler.drain(FluidHelperImpl.toFluidStack(resource), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE));
                }
            };
        }
    }

    public static IFluidTransfer getFluidTransfer(Level level, BlockPos pos, @Nullable Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                var handler = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction);
                if (handler.isPresent()) {
                    return toFluidTransfer(handler.orElse(null));
                }
            }
        }
        return null;
    }

    public static IFluidTransfer getFluidTransfer(IItemTransfer itemTransfer, int slot) {
        var itemStack = itemTransfer.getStackInSlot(slot);
        var handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (handler.isPresent()) {
            return toFluidTransfer(handler.orElse(null));
        }
        return null;
    }

    public static IFluidTransfer getFluidTransfer(Player player, AbstractContainerMenu screenHandler) {
        var itemStack = screenHandler.getCarried();
        var handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (handler.isPresent()) {
            return toFluidTransfer(handler.orElse(null));
        }
        return null;
    }

    public static IFluidTransfer getFluidTransfer(Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (handler.isPresent()) {
            return toFluidTransfer(handler.orElse(null));
        }
        return null;
    }

    public static IFluidTransfer getFluidTransfer(Player player, int slot) {
        var itemStack = player.getInventory().getItem(slot);
        var handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (handler.isPresent()) {
            return toFluidTransfer(handler.orElse(null));
        }
        return null;
    }

}
