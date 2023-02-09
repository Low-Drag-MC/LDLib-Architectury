package com.lowdragmc.lowdraglib.core.mixins.accessor;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.common.gui.elements.DrawableNineSliceTexture;
import mezz.jei.common.gui.recipes.ShapelessIcon;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import mezz.jei.common.ingredients.RegisteredIngredients;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeLayout.class,remap = false)
public interface RecipeLayoutAccessor {

    @Accessor("LOGGER")
    Logger getLogger();

    @Accessor
    int getIngredientCycleOffset();

    @Accessor
    RegisteredIngredients getRegisteredIngredients();

    @Accessor
    IIngredientVisibility getIngredientVisibility();

    @Accessor
    IModIdHelper getModIdHelper();

    @Accessor
    DrawableNineSliceTexture getRecipeBorder();

    @Accessor
    ShapelessIcon getShapelessIcon();

}
