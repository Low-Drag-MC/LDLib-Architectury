package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import lombok.Getter;

import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularSlotWidget
 */
public class ModularSlotWidget extends Widget {
    @Getter
    private Bounds bounds;
    @Getter
    private final IRecipeIngredientSlot slot;
    @Getter
    private final EmiRecipe recipe;

    public ModularSlotWidget(IRecipeIngredientSlot slot, Bounds bounds, EmiRecipe recipe) {
        this.bounds = bounds;
        this.slot = slot;
        this.recipe = recipe;
    }

    public void setLayout(int x, int y) {
        this.bounds = new Bounds(bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }



    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (bounds.contains(mouseX, mouseY)) {
            if (slotInteraction(bind -> bind.matchesMouse(button))) {
                return true;
            }
            if (slot.self().getGui().getModularUIGui() instanceof ModularWrapper modularWrapper) {
                if (slot.getXEIIngredientOverMouse(mouseX + modularWrapper.getLeft(), mouseY + modularWrapper.getTop()) instanceof EmiIngredient ingredient) {
                    return EmiScreenManager.stackInteraction(new EmiStackInteraction(ingredient, getRecipe(), true),
                            bind -> bind.matchesMouse(button));
                }
            }
        }
        return false;
    }

    public boolean canResolve() {
        EmiRecipe recipe = getRecipe();
        return recipe != null && recipe.supportsRecipeTree() && RecipeScreen.resolve != null;
    }

    public boolean slotInteraction(Function<EmiBind, Boolean> function) {
        if (canResolve()) {
            if (function.apply(EmiConfig.defaultStack)) {
                BoM.addRecipe(RecipeScreen.resolve, getRecipe());
                EmiHistory.pop();
                return true;
            } else if (function.apply(EmiConfig.viewRecipes)) {
                BoM.addResolution(RecipeScreen.resolve, getRecipe());
                EmiHistory.pop();
                return true;
            }
        }
        return false;
    }

    // fxxk EMI using refmap method
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        return EmiScreenManager.stackInteraction(new EmiStackInteraction(stack, getRecipe(), true),
//                bind -> bind.matchesKey(keyCode, scanCode));
//    }

}
