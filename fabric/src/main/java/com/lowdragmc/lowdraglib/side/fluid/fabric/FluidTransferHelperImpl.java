package com.lowdragmc.lowdraglib.side.fluid.fabric;

import com.lowdragmc.lowdraglib.msic.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.side.item.fabric.ItemTransferHelperImpl;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidTransferHelper
 */
public class FluidTransferHelperImpl {

    public static Storage<FluidVariant> toSingleStackStorage(IFluidTransfer fluidTransfer, int slot) {
        return new Storage<FluidVariant>() {

            @Override
            public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                return fluidTransfer.fill(FluidStack.create(resource.getFluid(), maxAmount), false);
            }

            @Override
            public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                return fluidTransfer.drain(FluidStack.create(resource.getFluid(), maxAmount), false).getAmount();
            }

            @Override
            public long simulateInsert(FluidVariant resource, long maxAmount, @org.jetbrains.annotations.Nullable TransactionContext transaction) {
                return fluidTransfer.fill(FluidStack.create(resource.getFluid(), maxAmount), true);
            }

            @Override
            public long simulateExtract(FluidVariant resource, long maxAmount, @org.jetbrains.annotations.Nullable TransactionContext transaction) {
                return fluidTransfer.drain(FluidStack.create(resource.getFluid(), maxAmount), true).getAmount();
            }

            @Override
            public Iterator<StorageView<FluidVariant>> iterator() {
                List<StorageView<FluidVariant>> views = new ArrayList<>();
                for (int i = 0; i < fluidTransfer.getTanks(); i++) {
                    final int index = i;
                    var stack = fluidTransfer.getFluidInTank(index);
                    var view = new SingleFluidStorage() {
                        @Override
                        protected long getCapacity(FluidVariant variant) {
                            return fluidTransfer.getTankCapacity(index);
                        }
                    };
                    view.variant = FluidVariant.of(stack.getFluid(), stack.getTag());
                    view.amount = stack.getAmount();
                }
                return views.iterator();
            }
        };
    }

    public static IFluidTransfer toFluidTransfer(Storage<FluidVariant> storage) {
        var iter = storage.iterator();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        while (iter.hasNext()) {
            views.add(iter.next());
        }
        return new IFluidTransfer() {

            @Override
            public int getTanks() {
                return views.size();
            }

            @NotNull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return FluidHelperImpl.toFluidStack(views.get(tank));
            }

            @Override
            public long getTankCapacity(int tank) {
                return views.get(tank).getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                boolean result;
                try (Transaction transaction = Transaction.openOuter()) {
                    result = storage.simulateExtract(FluidHelperImpl.toFluidVariant(stack), stack.getAmount(), transaction) > 0;
                }
                return result;
            }

            @Override
            public long fill(FluidStack resource, boolean simulate) {
                long filled;
                try (Transaction transaction = Transaction.openOuter()) {
                    filled = simulate ?
                            storage.simulateInsert(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction) :
                            storage.insert(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction);
                    transaction.commit();
                }
                return filled;
            }

            @NotNull
            @Override
            public FluidStack drain(FluidStack resource, boolean simulate) {
                var copied = resource.copy();
                try (Transaction transaction = Transaction.openOuter()) {
                    var drained = simulate ?
                            storage.simulateExtract(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction) :
                            storage.extract(FluidHelperImpl.toFluidVariant(resource), resource.getAmount(), transaction);
                    copied.setAmount(drained);
                    transaction.commit();
                }
                return copied;
            }

        };
    }

    public static IFluidTransfer getFluidTransfer(Level level, BlockPos pos, @Nullable Direction direction) {
        var storage = FluidStorage.SIDED.find(level, pos, direction);
        return storage == null ?  null : toFluidTransfer(storage);
    }

    public static IFluidTransfer getFluidTransfer(IItemTransfer itemTransfer, int slot) {
        var handContext = ContainerItemContext.ofSingleSlot(ItemTransferHelperImpl.toSingleStackStorage(itemTransfer, slot));
        var storage = handContext.find(FluidStorage.ITEM);
        return storage == null ?  null : toFluidTransfer(storage);
    }

    public static IFluidTransfer getFluidTransfer(Player player, AbstractContainerMenu screenHandler) {
        var handContext = ContainerItemContext.ofPlayerCursor(player, screenHandler);
        var storage = handContext.find(FluidStorage.ITEM);
        return storage == null ?  null : toFluidTransfer(storage);
    }

    public static IFluidTransfer getFluidTransfer(Player player, InteractionHand hand) {
        var handContext = ContainerItemContext.ofPlayerHand(player, hand);
        var storage = handContext.find(FluidStorage.ITEM);
        return storage == null ?  null : toFluidTransfer(storage);
    }

    public static IFluidTransfer getFluidTransfer(Player player, int slot) {
        var handContext = ContainerItemContext.ofPlayerSlot(player, PlayerInventoryStorage.of(player).getSlot(slot));
        var storage = handContext.find(FluidStorage.ITEM);
        return storage == null ?  null : toFluidTransfer(storage);
    }

    public static ItemStack getContainerItem(ItemStackTransfer copyContainer, IFluidTransfer handler) {
        return copyContainer.getStackInSlot(0);
    }

}
