package com.lowdragmc.lowdraglib.side.fluid;

import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidStack
 */
public class FluidStack {
    public static FluidStack empty() {
        return EMPTY;
    }

    public static FluidStack create(Fluid fluid, long amount, CompoundTag nbt) {
        return new FluidStack(fluid, amount, nbt);
    }

    public static FluidStack create(Fluid fluid, long amount) {
        return create(fluid, amount, null);
    }

    public static FluidStack create(FluidStack stack, long amount) {
        return create(stack.getFluid(), amount, stack.getTag());
    }

    private static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, null);
    private boolean isEmpty;
    private long amount;
    private CompoundTag tag;
    @Setter
    private Fluid fluid;

    private FluidStack(Fluid fluid, long amount, CompoundTag nbt) {
        this.fluid = fluid;
        this.amount = amount;
        if (nbt != null) {
            tag = nbt.copy();
        }
        updateEmpty();

    }

    public static FluidStack loadFromTag(CompoundTag nbt) {
        if (nbt == null) {
            return EMPTY;
        }
        if (!nbt.contains("FluidName", Tag.TAG_STRING)) {
            return EMPTY;
        }

        ResourceLocation fluidName = new ResourceLocation(nbt.getString("FluidName"));
        Fluid fluid = Registry.FLUID.get(fluidName);
        if (fluid == Fluids.EMPTY) {
            return EMPTY;
        }
        var stack = FluidStack.create(fluid, nbt.getLong("Amount"));

        if (nbt.contains("Tag", Tag.TAG_COMPOUND)) {
            stack.setTag(nbt.getCompound("Tag"));
        }
        return stack;
    }

    public static FluidStack readFromBuf(FriendlyByteBuf buf) {
        Fluid fluid = Registry.FLUID.get(new ResourceLocation(buf.readUtf()));
        int amount = buf.readVarInt();
        CompoundTag tag = buf.readNbt();
        if (fluid == Fluids.EMPTY) return EMPTY;
        return FluidStack.create(fluid, amount, tag);
    }

    protected void updateEmpty() {
        isEmpty = getRawFluid() == Fluids.EMPTY || amount <= 0;
    }

    public CompoundTag saveToTag(CompoundTag nbt) {
        nbt.putString("FluidName", Registry.FLUID.getKey(fluid).toString());
        nbt.putLong("Amount", amount);

        if (tag != null) {
            nbt.put("Tag", tag);
        }
        return nbt;
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeUtf(Registry.FLUID.getKey(fluid).toString());
        buf.writeVarLong(getAmount());
        buf.writeNbt(tag);
    }

    public Fluid getFluid() {
        return isEmpty ? Fluids.EMPTY : fluid;
    }

    public final Fluid getRawFluid() {
        return fluid;
    }

    public long getAmount() {
        return isEmpty ? 0 : amount;
    }

    public void setAmount(long amount) {
        if (fluid == Fluids.EMPTY) throw new IllegalStateException("Can't modify the empty stack.");
        this.amount = amount;
        updateEmpty();
    }

    public boolean hasTag() {
        return tag != null;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        if (getRawFluid() == Fluids.EMPTY) throw new IllegalStateException("Can't modify the empty stack.");
        this.tag = tag;
    }

    public boolean isEmpty() {
        return getAmount() == 0 || this == empty();
    }

    public Component getDisplayName() {
        return FluidHelper.getDisplayName(this);
    }

    public FluidStack copy() {
        return create(getFluid(), getAmount(), getTag());
    }

    public boolean isFluidEqual(@Nonnull FluidStack other) {
        return getFluid() == other.getFluid() && Objects.equals(getTag(), other.getTag());
    }

    public boolean isFluidStackEqual(@Nonnull FluidStack other) {
        return isFluidEqual(other) && getAmount() == other.getAmount();
    }

    public void grow(long amount) {
        setAmount(this.getAmount() + amount);
    }

    public void shrink(long amount) {
        setAmount(this.getAmount() - amount);
    }
}
