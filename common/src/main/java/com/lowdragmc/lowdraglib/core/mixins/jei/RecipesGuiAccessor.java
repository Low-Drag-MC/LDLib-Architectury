package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;


@Mixin(value = RecipesGui.class,remap = false)
public interface RecipesGuiAccessor {
    @Accessor
    List<RecipeLayout<?>> getRecipeLayouts();
}
