package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T wrapper, IFocusGroup focuses) {
        List<Widget> flatVisibleWidgetCollection = wrapper.modularUI.getFlatWidgetCollection();
        for (int i = 0; i < flatVisibleWidgetCollection.size(); i++) {
            Widget widget = flatVisibleWidgetCollection.get(i);
            if (widget instanceof IRecipeIngredientSlot slot) {
                IRecipeSlotBuilder slotBuilder = builder.addSlot(mapToRole(slot.getIngredientIO()), i, -1);
                Object ingredient = slot.getJEIIngredient();
                if (ingredient != null) {
                    slotBuilder.addIngredientsUnsafe(List.of(ingredient));
                }
            }
        }

    }

    private RecipeIngredientRole mapToRole(IngredientIO ingredientIO) {
        return switch (ingredientIO) {
            case INPUT -> RecipeIngredientRole.INPUT;
            case OUTPUT -> RecipeIngredientRole.OUTPUT;
            case CATALYST -> RecipeIngredientRole.CATALYST;
            case RENDER_ONLY -> RecipeIngredientRole.RENDER_ONLY;
        };
    }

}
