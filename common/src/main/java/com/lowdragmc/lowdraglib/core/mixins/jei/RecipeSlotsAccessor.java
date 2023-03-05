package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.ingredients.RecipeSlots;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;


@Mixin(value = RecipeSlots.class,remap = false)
public interface RecipeSlotsAccessor {
    @Mutable
    @Accessor
    void setSlots(List<RecipeSlot> slots);

    @Mutable
    @Accessor
    void setView(IRecipeSlotsView view);
}
