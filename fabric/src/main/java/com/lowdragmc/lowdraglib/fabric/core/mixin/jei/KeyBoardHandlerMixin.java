package com.lowdragmc.lowdraglib.fabric.core.mixin.jei;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote MouseHandlerMixin
 */
@Mixin(KeyboardHandler.class)
public abstract class KeyBoardHandlerMixin {

    @Redirect(method = "m_iajqjuwx",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z"))
    private static boolean injectKeyPressed(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyPressed(keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return screen.keyPressed(keyCode, scanCode, modifiers);
    }

    @Redirect(method = "m_iajqjuwx",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(III)Z"))
    private static boolean injectKeyReleased(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyReleased(keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return screen.keyReleased(keyCode, scanCode, modifiers);
    }

    @Redirect(method = "m_lmuidjri",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z"))
    private static boolean injectCharTyped(GuiEventListener listener, char codePoint, int modifiers) {
        if (Minecraft.getInstance().screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).charTyped(codePoint, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return listener.charTyped(codePoint, modifiers);
    }
    @Redirect(method = "m_hqjebvvw",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z"))
    private static boolean injectCharTyped2(GuiEventListener listener, char codePoint, int modifiers) {
        if (Minecraft.getInstance().screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).charTyped(codePoint, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return listener.charTyped(codePoint, modifiers);
    }

}
