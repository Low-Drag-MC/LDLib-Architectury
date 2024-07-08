package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipeGuiLayouts.class, remap = false)
public interface RecipeGuiLayoutsAccessor {

    @Accessor
    List<RecipeLayoutWithButtons<?>> getRecipeLayoutsWithButtons();
}
