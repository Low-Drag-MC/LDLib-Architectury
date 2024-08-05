package com.lowdragmc.lowdraglib.core.mixins.jei;

import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.gui.recipes.ShapelessIcon;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipeLayout.class,remap = false)
public interface RecipeLayoutAccessor {

    @Accessor("LOGGER")
    Logger getLogger();

    @Accessor
    ShapelessIcon getShapelessIcon();


    @Accessor
    ImmutableRect2i getArea();

    @Accessor
    IScalableDrawable getRecipeBackground();

    @Accessor
    int getRecipeBorderPadding();

    @Accessor
    List<IRecipeSlotDrawable> getAllSlots();
}
