package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
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
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    private static void addJEISlot(IRecipeLayoutBuilder builder, IRecipeIngredientSlot slot, RecipeIngredientRole role, int index) {
        var slotName = "slot_" + index;
        IRecipeSlotBuilder slotBuilder = builder.addSlotToWidget(role, (extrasBuilder, recipe, slots) -> {
            var jeiSlot = slots.stream().filter(s-> s.getSlotName().map(name -> name.equals(slotName)).orElse(false)).findFirst();
            jeiSlot.ifPresent(drawable -> extrasBuilder.addWidget(new SlotRecipeWidget(slot, drawable)));
        });
        // append ingredients
        for (Object ingredient : slot.getXEIIngredients()) {
            if (ingredient instanceof IClickableIngredient clickableIngredient) {
                var type = clickableIngredient.getTypedIngredient().getType();
                var ingredients = clickableIngredient.getTypedIngredient().getIngredient();
                slotBuilder.addIngredient(type, ingredients);
            }
        }
        // set slot name
        slotBuilder.setSlotName(slotName);
        // append widget tooltips
        slotBuilder.addRichTooltipCallback((recipeSlotView, tooltipBuilder) -> tooltipBuilder.addAll(slot.getAdditionalToolTips(new ArrayList<>())));
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
        recipe.draw(guiGraphics, (int) mouseX, (int) mouseY, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());
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
