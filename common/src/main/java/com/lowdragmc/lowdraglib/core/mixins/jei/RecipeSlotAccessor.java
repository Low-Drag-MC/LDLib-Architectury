package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.ingredients.RendererOverrides;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(value = RecipeSlot.class, remap = false)
public interface RecipeSlotAccessor {
    
    @Accessor("rect")
    void setArea(ImmutableRect2i rect);
    
    @Accessor
    List<IRecipeSlotTooltipCallback> getTooltipCallbacks();
    
    @Accessor
    List<Optional<ITypedIngredient<?>>> getAllIngredients();
    
    @Accessor
    List<Optional<ITypedIngredient<?>>> getDisplayIngredients();


    @Accessor
    @Nullable RendererOverrides getRendererOverrides();
    @Accessor
    @Nullable IDrawable getBackground();
    @Accessor
    @Nullable IDrawable getOverlay();
    @Accessor
    @Nullable String getSlotName();
}
