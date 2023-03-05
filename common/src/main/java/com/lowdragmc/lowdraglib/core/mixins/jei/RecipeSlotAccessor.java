package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeSlot.class,remap = false)
public interface RecipeSlotAccessor {
    @Accessor("rect")
    void setArea(ImmutableRect2i rect);

    @Accessor
    IIngredientManager getIngredientManager();
}
