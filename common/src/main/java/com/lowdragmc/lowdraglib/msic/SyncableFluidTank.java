package com.lowdragmc.lowdraglib.msic;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2022/10/26
 * @implNote SyncableFluidStackHandler
 */
@Accessors(chain = true)
public class SyncableFluidTank extends FluidTank implements IContentChangeAware<SyncableFluidTank>, ITagSerializable<CompoundTag> {
    @Getter
    @Setter
    private Runnable onContentsChanged = () -> {};

    public SyncableFluidTank(int capacity) {
        super(capacity);
    }

    public SyncableFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        onContentsChanged.run();
    }

    @Override
    public CompoundTag serializeNBT() {
        return writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readFromNBT(nbt);
    }
}
