package com.lowdragmc.lowdraglib.msic;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/25
 * @implNote FluidTransferList
 */
public class FluidTransferList implements IFluidTransfer, ITagSerializable<CompoundTag> {
    public final IFluidTransfer[] transfers;

    public FluidTransferList(IFluidTransfer... transfers) {
        this.transfers = transfers;
    }

    public FluidTransferList(List<IFluidTransfer> transfers) {
        this.transfers = transfers.toArray(IFluidTransfer[]::new);
    }

    @Override
    public int getTanks() {
        return Arrays.stream(transfers).mapToInt(IFluidTransfer::getTanks).sum();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        int index = 0;
        for (IFluidTransfer transfer : transfers) {
            if (tank - index < transfer.getTanks()) {
                return transfer.getFluidInTank(tank - index);
            }
            index += transfer.getTanks();
        }
        return FluidStack.empty();
    }

    @Override
    public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {
        int index = 0;
        for (IFluidTransfer transfer : transfers) {
            if (tank - index < transfer.getTanks()) {
                transfer.setFluidInTank(tank - index, fluidStack);
                return;
            }
            index += transfer.getTanks();
        }
    }

    @Override
    public long getTankCapacity(int tank) {
        int index = 0;
        for (IFluidTransfer transfer : transfers) {
            if (tank - index < transfer.getTanks()) {
                return transfer.getTankCapacity(tank - index);
            }
            index += transfer.getTanks();
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        int index = 0;
        for (IFluidTransfer transfer : transfers) {
            if (tank - index < transfer.getTanks()) {
                return transfer.isFluidValid(tank - index, stack);
            }
            index += transfer.getTanks();
        }
        return false;
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (resource.isEmpty()) return 0;
        var copied = resource.copy();
        for (var transfer : transfers) {
            var candidate = copied.copy();
            copied.shrink(transfer.fill(candidate, simulate));
            if (copied.isEmpty()) break;
        }
        return resource.getAmount() - copied.getAmount();
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, boolean simulate) {
        if (resource.isEmpty()) return FluidStack.empty();
        var copied = resource.copy();
        for (var transfer : transfers) {
            var candidate = copied.copy();
            copied.shrink(transfer.drain(candidate, simulate).getAmount());
            if (copied.isEmpty()) break;
        }
        copied.setAmount(resource.getAmount() - copied.getAmount());
        return copied;
    }

    @NotNull
    @Override
    public FluidStack drain(long maxDrain, boolean simulate) {
        if (maxDrain == 0) {
            return FluidStack.empty();
        }
        FluidStack totalDrained = null;
        for (var storage : transfers) {
            if (totalDrained == null || totalDrained.isEmpty()) {
                totalDrained = storage.drain(maxDrain, simulate);
                if (totalDrained.isEmpty()) {
                    totalDrained = null;
                } else {
                    maxDrain -= totalDrained.getAmount();
                }
            } else {
                FluidStack copy = totalDrained.copy();
                copy.setAmount(maxDrain);
                FluidStack drain = storage.drain(copy, simulate);
                totalDrained.grow(drain.getAmount());
                maxDrain -= drain.getAmount();
            }
            if (maxDrain <= 0) break;
        }
        return totalDrained == null ? FluidStack.empty() : totalDrained;
    }

    @Override
    public final void onContentsChanged() {
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        var list = new ListTag();
        for (IFluidTransfer transfer : transfers) {
            if (transfer instanceof ITagSerializable<?> serializable) {
                list.add(serializable.serializeNBT());
            } else {
                LDLib.LOGGER.warn("[FluidTransferList] internal tank doesn't support serialization");
            }
        }
        tag.put("tanks", list);
        tag.putByte("type", list.getElementType());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var list = nbt.getList("tanks", nbt.getByte("type"));
        for (int i = 0; i < list.size(); i++) {
            if (transfers[i] instanceof ITagSerializable serializable) {
                serializable.deserializeNBT(list.get(i));
            } else {
                LDLib.LOGGER.warn("[FluidTransferList] internal tank doesn't support serialization");
            }
        }
    }
}
