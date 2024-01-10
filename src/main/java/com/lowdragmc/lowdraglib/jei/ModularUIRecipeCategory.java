package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import lombok.val;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                var role = mapToRole(slot.getIngredientIO());
                if (role == null) { // both
                    addJEISlot(builder, slot, RecipeIngredientRole.INPUT, i);
                    addJEISlot(builder, slot, RecipeIngredientRole.OUTPUT, i);
                } else {
                    addJEISlot(builder, slot, role, i);
                }
            }
        }

    }

    private static void addJEISlot(IRecipeLayoutBuilder builder, IRecipeIngredientSlot slot, RecipeIngredientRole role, int index) {
        IRecipeSlotBuilder slotBuilder = builder.addSlot(role, index, -1);
        Map<IIngredientType, List> map = new HashMap<>();
        slot.getXEIIngredients().stream()
                .filter(IClickableIngredient.class::isInstance)
                .map(IClickableIngredient.class::cast)
                .forEach(clickableIngredient -> map.computeIfAbsent(clickableIngredient.getTypedIngredient().getType(), a -> new ArrayList<>()).add(clickableIngredient.getTypedIngredient().getIngredient()));
        map.forEach((ingredient, list) -> slotBuilder.addIngredients(ingredient, list));
    }

    @Nullable
    private RecipeIngredientRole mapToRole(IngredientIO ingredientIO) {
        return switch (ingredientIO) {
            case INPUT -> RecipeIngredientRole.INPUT;
            case OUTPUT -> RecipeIngredientRole.OUTPUT;
            case CATALYST -> RecipeIngredientRole.CATALYST;
            case RENDER_ONLY -> RecipeIngredientRole.RENDER_ONLY;
            case BOTH -> null;
        };
    }

}
