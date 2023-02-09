package com.lowdragmc.lowdraglib.client.renderer;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IItemRendererProvider {
    ThreadLocal<Boolean> disabled = ThreadLocal.withInitial(()->false);

    @Nullable
    IRenderer getRenderer(ItemStack stack);
}
