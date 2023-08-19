package com.lowdragmc.lowdraglib.misc;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidStorage
 */
public class FluidStorage implements IFluidStorage, IContentChangeAware, ITagSerializable<CompoundTag> {
    @Getter
    @Setter
    private Runnable onContentsChanged = () -> {
    };
    @Setter
    protected Predicate<FluidStack> validator;

    @Getter
    @Nonnull
    protected FluidStack fluid = FluidStack.empty();
    @Setter
    @Getter
    protected long capacity;

    public FluidStorage(long capacity) {
        this(capacity, e -> true);
    }

    public FluidStorage(long capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidStorage(FluidStack fluidStack) {
        this(fluidStack.getAmount());
        fluid = fluidStack;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
        onContentsChanged();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChange) {
        if (tank >= getTanks() || resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (simulate) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = FluidStack.create(resource, Math.min(capacity, resource.getAmount()));
            if (notifyChange) {
                onContentsChanged();
            }
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        long filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0 && notifyChange)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChange) {
        if (tank >= getTanks() || resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.empty();
        }
        return drain(resource.getAmount(), simulate, notifyChange);
    }

    public void onContentsChanged() {
        onContentsChanged.run();
    }

    @NotNull
    @Override
    public Object createSnapshot() {
        return fluid.copy();
    }

    @Override
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof FluidStack stack) {
            this.fluid = stack.copy();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        return fluid.saveToTag(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setFluid(FluidStack.loadFromTag(nbt));
    }

    public FluidStorage copy() {
        var storage = new FluidStorage(capacity, validator);
        storage.setFluid(fluid.copy());
        return storage;
    }

    @Override
    public boolean supportsFill(int tank) {
        return true;
    }

    @Override
    public boolean supportsDrain(int tank) {
        return true;
    }
}
