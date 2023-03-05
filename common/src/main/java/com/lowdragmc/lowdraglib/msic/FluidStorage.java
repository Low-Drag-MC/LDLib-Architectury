package com.lowdragmc.lowdraglib.msic;

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
    public long fill(FluidStack resource, boolean simulate) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
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
            onContentsChanged();
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
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, boolean simulate) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.empty();
        }
        return drain(resource.getAmount(), simulate);
    }

    public void onContentsChanged() {
        onContentsChanged.run();
    }

    @Override
    public CompoundTag serializeNBT() {
        return fluid.saveToTag(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setFluid(FluidStack.loadFromTag(nbt));
    }
}
