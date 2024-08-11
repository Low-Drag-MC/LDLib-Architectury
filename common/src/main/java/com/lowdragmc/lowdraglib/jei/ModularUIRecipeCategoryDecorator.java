package com.lowdragmc.lowdraglib.jei;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ModularUIRecipeCategoryDecorator<T extends ModularWrapper<?>> implements IRecipeCategoryDecorator<T> {

    @Override
    public void draw(T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        recipe.draw(guiGraphics, (int) mouseX, (int) mouseY, Minecraft.getInstance().getFrameTime());
    }

    @Override
    public void decorateTooltips(ITooltipBuilder tooltip, T recipe, IRecipeCategory<T> recipeCategory, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.tooltipTexts != null && !recipe.tooltipTexts.isEmpty()) {
            tooltip.addAll(recipe.tooltipTexts);
        }
        if (recipe.tooltipComponent != null) {
            tooltip.add(recipe.tooltipComponent);
        }
    }

}
