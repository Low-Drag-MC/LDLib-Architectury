package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

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
        var slotBuilder = builder.addSlot(role, slot.self().getPositionX(), slot.self().getPositionY());
        // append ingredients
        var ingredientMap = new HashMap<IIngredientType, List>();
        for (Object ingredient : slot.getXEIIngredients()) {
            if (ingredient instanceof IClickableIngredient<?> clickableIngredient) {
                ingredientMap.computeIfAbsent(clickableIngredient.getTypedIngredient().getType(), k -> new ArrayList())
                        .add(clickableIngredient.getTypedIngredient().getIngredient());
            }
        }
        for (var entry : ingredientMap.entrySet()) {
            var type = entry.getKey();
            var ingredients = entry.getValue();
            slotBuilder.addIngredients(type, ingredients);
            slotBuilder.setCustomRenderer(type, new IIngredientRenderer<>() {
                @Override
                public void render(GuiGraphics guiGraphics, Object ingredient) {
                    slot.setCurrentJEIRenderedIngredient(ingredient);
                }

                @Override
                public List<Component> getTooltip(Object ingredient, TooltipFlag tooltipFlag) {
                    return Collections.emptyList();
                }

                @Override
                public void getTooltip(ITooltipBuilder tooltip, Object ingredient, TooltipFlag tooltipFlag) {
                    tooltip.addAll(slot.getFullTooltipTexts());
                }

                @Override
                public int getWidth() {
                    return slot.self().getSizeWidth();
                }

                @Override
                public int getHeight() {
                    return slot.self().getSizeHeight();
                }
            });
        }
        // set slot name
        slotBuilder.setSlotName(slotName);
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
