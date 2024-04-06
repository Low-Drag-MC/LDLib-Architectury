package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagOrCycleFluidTransfer implements IFluidTransfer {
    @Getter
    private List<Either<List<Pair<TagKey<Fluid>, Long>>, List<FluidStack>>> stacks;

    private List<List<FluidStack>> unwrapped = null;


    public TagOrCycleFluidTransfer(List<Either<List<Pair<TagKey<Fluid>, Long>>, List<FluidStack>>> stacks) {
        updateStacks(stacks);
    }

    public void updateStacks(List<Either<List<Pair<TagKey<Fluid>, Long>>, List<FluidStack>>> stacks) {
        this.stacks = new ArrayList<>(stacks);
        this.unwrapped = null;
    }

    public List<List<FluidStack>> getUnwrapped() {
        if (unwrapped == null) {
            unwrapped = stacks.stream()
                    .map(tagOrFluid -> tagOrFluid.map(
                            tagList -> tagList
                                    .stream()
                                    .flatMap(pair -> BuiltInRegistries.FLUID.getTag(pair.getFirst())
                                            .map(holderSet -> holderSet.stream()
                                                    .map(holder -> FluidStack.create(holder.value(), pair.getSecond())))
                                            .orElseGet(Stream::empty))
                                    .toList(),
                            Function.identity()))
                    .collect(Collectors.toList());
        }
        return unwrapped;
    }

    @Override
    public int getTanks() {
        return stacks.size();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        List<FluidStack> stackList = getUnwrapped().get(tank);
        return stackList == null || stackList.isEmpty() ? FluidStack.empty() : stackList.get(Math.abs((int)(System.currentTimeMillis() / 1000) % stackList.size()));
    }

    @Override
    public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {
        if (tank >= 0 && tank < stacks.size()) {
            stacks.set(tank, Either.right(List.of(fluidStack)));
            unwrapped = null;
        }
    }

    @Override
    public long getTankCapacity(int tank) {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        return 0;
    }

    @Override
    public boolean supportsFill(int tank) {
        return false;
    }

    @NotNull
    @Override
    public FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        return FluidStack.empty();
    }

    @Override
    public boolean supportsDrain(int tank) {
        return false;
    }

    @NotNull
    @Override
    public Object createSnapshot() {
        return new Object();
    }

    @Override
    public void restoreFromSnapshot(Object snapshot) {

    }
}
