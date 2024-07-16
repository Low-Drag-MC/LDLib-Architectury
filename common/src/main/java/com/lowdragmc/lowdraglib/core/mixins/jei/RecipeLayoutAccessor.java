package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.gui.elements.DrawableNineSliceTexture;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.gui.recipes.ShapelessIcon;
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
    ShapelessIcon getShapelessIcon();


    @Accessor
    ImmutableRect2i getArea();

    @Accessor
    IScalableDrawable getRecipeBackground();
}
