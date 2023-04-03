package com.lowdragmc.lowdraglib.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularEmiRecipeCategory
 */
public class ModularUIEmiRecipeCategory extends EmiRecipeCategory {
    public ModularUIEmiRecipeCategory(ResourceLocation id, EmiRenderable icon) {
        super(id, icon);
    }

    public ModularUIEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified) {
        super(id, icon, simplified);
    }

    public ModularUIEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter) {
        super(id, icon, simplified, sorter);
    }

}
