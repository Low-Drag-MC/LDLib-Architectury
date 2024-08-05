package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.core.mixins.RecipeGuiLayoutsAccessor;
import com.lowdragmc.lowdraglib.core.mixins.jei.RecipesGuiAccessor;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIJeiHandler;
import com.lowdragmc.lowdraglib.test.TestJEIPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.input.ClickableIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.library.ingredients.TypedIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote jei plugin
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime;
    public static IJeiHelpers jeiHelpers;
    private static final ModularUIJeiHandler modularUIGuiHandler = new ModularUIJeiHandler();

    public JEIPlugin() {
        LDLib.LOGGER.debug("LDLib JEI Plugin created");
    }

    public static Object getItemIngredient(ItemStack itemStack, int x, int y, int width, int height) {
        return new ClickableIngredient<>(TypedIngredient.createUnvalidated(VanillaTypes.ITEM_STACK, itemStack),
                new ImmutableRect2i(x, y, width, height));
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<IRecipeLayoutDrawable<?>> getRecipeLayouts(RecipesGui recipesGui) {
        RecipeGuiLayouts layouts = ((RecipesGuiAccessor) recipesGui).getLayouts();
        return (List) ((RecipeGuiLayoutsAccessor) layouts).getRecipeLayoutsWithButtons().stream()
                .map(RecipeLayoutWithButtons::recipeLayout)
                .toList();
    }

    public static boolean isJeiEnabled() {
        return jeiRuntime != null && jeiRuntime.getIngredientListOverlay().isListDisplayed();
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JEIPlugin.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        if (LDLib.isReiLoaded() || LDLib.isEmiLoaded()) return;
        registration.addGhostIngredientHandler(ModularUIGuiContainer.class, modularUIGuiHandler);
        registration.addGenericGuiContainerHandler(ModularUIGuiContainer.class, modularUIGuiHandler);
    }

    @Override
    public void registerAdvanced(@Nonnull IAdvancedRegistration registration) {
    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LDLib.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        JEIPlugin.jeiHelpers = registration.getJeiHelpers();
        if (Platform.isDevEnv()) {
            TestJEIPlugin.registerCategories(registration);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Platform.isDevEnv()) {
            TestJEIPlugin.registerRecipes(registration);
        }
    }
}
