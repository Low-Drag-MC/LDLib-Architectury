package com.lowdragmc.lowdraglib.fabric.core.mixin.jei;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote MouseHandlerMixin
 */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Redirect(method = "m_jzgvmppg",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z"))
    private static boolean injectClick(Screen screen, double x, double y, int button) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseClicked(x, y, button)) {
                        return true;
                    }
                }
            }
        }
        return screen.mouseClicked(x, y, button);
    }

    @Redirect(method = "m_acizjdos",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseReleased(DDI)Z"))
    private static boolean injectReleased(Screen screen, double x, double y, int button) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                if (recipeLayout.getRecipe() instanceof ModularWrapper wrapper) {
                    wrapper.mouseReleased(x, y, button);
                }
            }
        }
        return screen.mouseReleased(x, y, button);
    }

    @Redirect(method = "m_pygajgfg",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(DDIDD)Z"))
    private static boolean injectMouseDragged(Screen screen, double x, double y, int button, double dx, double dy) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                if (recipeLayout instanceof RecipeLayoutWrapper recipeLayoutWrapper){
                    if (recipeLayoutWrapper.getWrapper().mouseDragged(x, y, button, dx, dy)) {
                        recipeLayoutWrapper.onPositionUpdate();
                        return true;
                    }
                }
            }
        }
        return screen.mouseDragged(x, y, button, dx, dy);
    }

    @Redirect(method = "onScroll",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z"))
    private static boolean injectMouseScroll(Screen screen, double x, double y, double delta) {
        if (screen instanceof RecipesGui recipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
                if (recipeLayout instanceof RecipeLayoutWrapper recipeLayoutWrapper){
                    if (recipeLayoutWrapper.getWrapper().mouseScrolled(x, y, delta)) {
                        recipeLayoutWrapper.onPositionUpdate();
                        return true;
                    }
                }
            }
        }
        return screen.mouseScrolled(x, y, delta);
    }
}
