package com.lowdragmc.lowdraglib.side.fluid;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidActionResult
 */
public class FluidActionResult {
    public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemStack.EMPTY);

    public final boolean success;
    @Nonnull
    public final ItemStack result;

    public FluidActionResult(@Nonnull ItemStack result)
    {
        this(true, result);
    }

    private FluidActionResult(boolean success, @Nonnull ItemStack result)
    {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess()
    {
        return success;
    }

    @Nonnull
    public ItemStack getResult()
    {
        return result;
    }
}
