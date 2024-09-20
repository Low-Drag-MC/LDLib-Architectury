package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    private static void addJEISlot(IRecipeLayoutBuilder builder, IRecipeIngredientSlot slot, RecipeIngredientRole role, int index) {
        Map<IIngredientType, List> map = new HashMap<>();
        slot.getXEIIngredients().stream()
                .filter(IClickableIngredient.class::isInstance)
                .map(IClickableIngredient.class::cast)
                .forEach(clickableIngredient -> map.computeIfAbsent(clickableIngredient.getTypedIngredient().getType(),
                        a -> new ArrayList<>()).add(clickableIngredient.getTypedIngredient().getIngredient()));
        IRecipeSlotBuilder slotBuilder = builder.addSlotToWidget(role, (extrasBuilder, recipe, slots) -> {
            extrasBuilder.addWidget(new SlotRecipeWidget(slot, slot.self().getPositionX(), slot.self().getPositionY()));
        });
        map.forEach(slotBuilder::addIngredients);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T wrapper, IFocusGroup focuses) {
        wrapper.setRecipeWidget(0, 0);
        List<Widget> flatVisibleWidgetCollection = wrapper.modularUI.getFlatWidgetCollection();
        for (int i = 0; i < flatVisibleWidgetCollection.size(); i++) {
            var widget = flatVisibleWidgetCollection.get(i);
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

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T wrapper, IFocusGroup focuses) {
        builder.addGuiEventListener(new ModularUIGuiEventListener<>(wrapper));
        builder.addWidget(new ModularForegroundRecipeWidget(wrapper));
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        recipe.draw(guiGraphics, (int) mouseX, (int) mouseY, Minecraft.getInstance().getFrameTime());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IRecipeCategory.super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if (recipe.tooltipTexts != null && !recipe.tooltipTexts.isEmpty()) {
            tooltip.addAll(recipe.tooltipTexts);
        }
        if (recipe.tooltipComponent != null) {
            tooltip.add(recipe.tooltipComponent);
        }
    }

    @Nullable
    public static RecipeIngredientRole mapToRole(IngredientIO ingredientIO) {
        return switch (ingredientIO) {
            case INPUT -> RecipeIngredientRole.INPUT;
            case OUTPUT -> RecipeIngredientRole.OUTPUT;
            case CATALYST -> RecipeIngredientRole.CATALYST;
            case RENDER_ONLY -> RecipeIngredientRole.RENDER_ONLY;
            case BOTH -> null;
        };
    }

}
