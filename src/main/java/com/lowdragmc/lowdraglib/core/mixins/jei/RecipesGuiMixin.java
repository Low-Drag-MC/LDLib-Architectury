package com.lowdragmc.lowdraglib.core.mixins.jei;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.library.gui.recipes.RecipeLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote MouseHandlerMixin
 */
@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {

    protected RecipesGuiMixin(Component title) {
        super(title);
    }

    @Inject(method = "m_6375_", at = @At(value = "HEAD"), cancellable = true)
    private void ldlib$injectClick(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            Object recipe = recipeLayout.getRecipe();
            if (recipe instanceof ModularWrapper) {
                if (((ModularWrapper<?>) recipe).mouseClicked(mouseX, mouseY, mouseButton)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            if (recipeLayout.getRecipe() instanceof ModularWrapper<?> wrapper) {
                wrapper.mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            if (recipeLayout instanceof RecipeLayoutWrapper<?> recipeLayoutWrapper){
                if (recipeLayoutWrapper.getWrapper().mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    recipeLayoutWrapper.onPositionUpdate();
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Inject(method = "m_6050_", at = @At(value = "HEAD"), cancellable = true)
    private void ldlib$injectMouseScroll(double scrollX, double scrollY, double horizontal, double vertical, CallbackInfoReturnable<Boolean> cir) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            if (recipeLayout instanceof RecipeLayoutWrapper<?> recipeLayoutWrapper){
                if (recipeLayoutWrapper.getWrapper().mouseScrolled(scrollX, scrollY, horizontal, vertical)) {
                    recipeLayoutWrapper.onPositionUpdate();
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "m_7933_", at = @At(value = "HEAD"), cancellable = true)
    private void ldlib$injectKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            Object recipe = recipeLayout.getRecipe();
            if (recipe instanceof ModularWrapper) {
                if (((ModularWrapper<?>) recipe).keyPressed(keyCode, scanCode, modifiers)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            Object recipe = recipeLayout.getRecipe();
            if (recipe instanceof ModularWrapper) {
                if (((ModularWrapper<?>) recipe).keyReleased(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        var recipesGui = RecipesGui.class.cast(this);
        for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts(recipesGui)) {
            Object recipe = recipeLayout.getRecipe();
            if (recipe instanceof ModularWrapper) {
                if (((ModularWrapper<?>) recipe).charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

}
