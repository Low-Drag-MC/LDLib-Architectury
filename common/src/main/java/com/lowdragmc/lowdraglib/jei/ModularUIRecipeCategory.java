package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T wrapper, IFocusGroup focuses) {
        wrapper.setRecipeWidget(0, 0);

        List<Widget> flatVisibleWidgetCollection = wrapper.modularUI.getFlatWidgetCollection();
        for (Widget widget : flatVisibleWidgetCollection) {
            if (widget instanceof IRecipeIngredientSlot slot) {
                var role = mapToRole(slot.getIngredientIO());
                Position pos = widget.getPosition();
                IRecipeSlotBuilder slotBuilder;
                if (widget.getParent() instanceof DraggableScrollableWidgetGroup) {
                    // don't add the JEI widget at all if we have a draggable group, let the draggable widget handle it instead.
                    if (role == null) {
                        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredientsUnsafe(slot.getXEIIngredients());
                        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredientsUnsafe(slot.getXEIIngredients());
                        builder.addSlotToWidget(RecipeIngredientRole.INPUT, (builder1, recipe, slots) -> {}).addIngredientsUnsafe(slot.getXEIIngredients());
                        builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, (builder1, recipe, slots) -> {}).addIngredientsUnsafe(slot.getXEIIngredients());
                    } else {
                        builder.addInvisibleIngredients(role).addIngredientsUnsafe(slot.getXEIIngredients());
                        builder.addSlotToWidget(role, (builder1, recipe, slots) -> {}).addIngredientsUnsafe(slot.getXEIIngredients());
                    }
                    continue;
                }
                if (role == null) { // both
                    addJEISlot(builder, slot, RecipeIngredientRole.INPUT, pos.x, pos.y);
                    slotBuilder = addJEISlot(builder, slot, RecipeIngredientRole.OUTPUT, pos.x, pos.y);
                } else {
                    slotBuilder = addJEISlot(builder, slot, role, pos.x, pos.y);
                }
                int width = widget.getSize().width;
                int height = widget.getSize().height;

                slotBuilder.setBackground(IGui2IDrawable.toDrawable(widget.getBackgroundTexture(), width, height), -1, -1);
                slotBuilder.setOverlay(IGui2IDrawable.toDrawable(widget.getOverlay(), width, height), -1, -1);
                widget.setActive(false);
                widget.setVisible(false);
                if (slot instanceof com.lowdragmc.lowdraglib.gui.widget.SlotWidget slotW) {
                    slotW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                } else if (slot instanceof com.lowdragmc.lowdraglib.gui.widget.TankWidget tankW) {
                    tankW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                    slotBuilder.setFluidRenderer(1, false, width - 2, height - 2);
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

    private static IRecipeSlotBuilder addJEISlot(IRecipeLayoutBuilder builder, IRecipeIngredientSlot slot, RecipeIngredientRole role, int x, int y) {
        IRecipeSlotBuilder slotBuilder = builder.addSlot(role, x, y);
        slotBuilder.addIngredientsUnsafe(slot.getXEIIngredients());
        return slotBuilder;
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
