package com.lowdragmc.lowdraglib.core.mixins.jei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.library.gui.recipes.RecipeLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;

@Mixin(value = RecipeLayout.class, remap = false)
public abstract class JeiRecipeLayoutMixin {

    @SuppressWarnings({"unchecked"})
    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private static <T> void injectCreate(
            IRecipeCategory<T> recipeCategory,
            Collection<IRecipeCategoryDecorator<T>> decorators,
            T recipe,
            IFocusGroup focuses,
            IIngredientManager ingredientManager,
            IIngredientVisibility ingredientVisibility,
            IModIdHelper modIdHelper,
            Textures textures,
            CallbackInfoReturnable<Optional<IRecipeLayoutDrawable<T>>> cir
    ) {
        if (recipe instanceof ModularWrapper<?> && recipeCategory != null) {
            RecipeLayout<?> recipeLayoutWrapper = RecipeLayoutWrapper.createWrapper(
                    recipeCategory,
                    decorators,
                    recipe,
                    focuses,
                    ingredientManager,
                    ingredientVisibility,
                    modIdHelper,
                    textures);
            cir.setReturnValue(Optional.ofNullable((IRecipeLayoutDrawable<T>) recipeLayoutWrapper));
        }
    }

}
