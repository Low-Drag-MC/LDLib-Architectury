package com.lowdragmc.lowdraglib.core.mixins.jei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.ingredients.RegisteredIngredients;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeLayout.class, remap = false)
public abstract class JeiRecipeLayoutMixin {

    @SuppressWarnings({"unchecked"})
    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private static <T> void injectCreate(
            int index,
            IRecipeCategory<T> recipeCategory,
            T recipe,
            IFocusGroup focuses,
            RegisteredIngredients registeredIngredients,
            IIngredientVisibility ingredientVisibility,
            IModIdHelper modIdHelper,
            int posX, int posY,
            Textures textures,
            CallbackInfoReturnable<@Nullable RecipeLayout<T>> cir
    ) {
        if (recipe instanceof ModularWrapper<?> wrapper && recipeCategory != null) {
            IRecipeCategory<ModularWrapper<?>> category = (IRecipeCategory<ModularWrapper<?>>) recipeCategory;
            RecipeLayout<T> recipeLayoutWrapper = (RecipeLayout<T>) RecipeLayoutWrapper.createWrapper(index, category, wrapper, focuses, registeredIngredients, ingredientVisibility, modIdHelper, posX, posY, textures);
            cir.setReturnValue(recipeLayoutWrapper);
        }
    }

}
