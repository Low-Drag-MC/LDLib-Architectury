package com.lowdragmc.lowdraglib.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.EmiHistory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularSlotWidget
 */
public class ModularSlotWidget extends Widget {
    private Bounds bounds;
    private final EmiIngredient stack;
    private final EmiRecipe recipe;

    public ModularSlotWidget(EmiIngredient stack, Bounds bounds, EmiRecipe recipe) {
        this.bounds = bounds;
        this.stack = stack;
        this.recipe = recipe;
    }

    public void setLayout(int x, int y) {
        this.bounds = new Bounds(bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {

    }

    public EmiRecipe getRecipe() {
        return recipe;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (bounds.contains(mouseX, mouseY)) {
            if (button == 0 && getRecipe() != null && getRecipe().supportsRecipeTree() && RecipeScreen.resolve != null) {
                BoM.addResolution(RecipeScreen.resolve, getRecipe());
                EmiHistory.pop();
                return true;
            } else {
                return EmiScreenManager.stackInteraction(new EmiStackInteraction(stack, getRecipe(), true), bind -> bind.matchesMouse(button));
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return EmiScreenManager.stackInteraction(new EmiStackInteraction(stack, getRecipe(), true),
                bind -> bind.matchesKey(keyCode, scanCode));
    }

}
