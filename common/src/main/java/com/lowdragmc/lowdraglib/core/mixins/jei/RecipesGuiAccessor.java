package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = RecipesGui.class,remap = false)
public interface RecipesGuiAccessor {
    @Accessor
    RecipeGuiLayouts getLayouts();
}
