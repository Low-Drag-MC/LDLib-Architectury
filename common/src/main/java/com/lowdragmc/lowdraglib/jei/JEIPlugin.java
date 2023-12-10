package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.core.mixins.jei.RecipesGuiAccessor;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIJeiHandler;
import com.lowdragmc.lowdraglib.test.TestJEIPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.input.ClickableIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.ingredients.TypedIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote jei plugin
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    
    public static IJeiRuntime jeiRuntime;
    private static final ModularUIJeiHandler modularUIGuiHandler = new ModularUIJeiHandler();

    public JEIPlugin() {
        LDLib.LOGGER.debug("LDLib JEI Plugin created");
    }

    public static Object getItemIngredient(ItemStack itemStack, int x, int y, int width, int height) {
        Supplier<Object> supplier = () -> {
            return new ClickableIngredient<>(TypedIngredient.createUnvalidated(VanillaTypes.ITEM_STACK, itemStack),
                    new ImmutableRect2i(x, y, width, height));
        };
        return supplier.get();
    }

    @Nonnull
    public static List<RecipeLayout<?>> getRecipeLayouts(RecipesGui recipesGui) {
        return new ArrayList<>(((RecipesGuiAccessor)recipesGui).getRecipeLayouts());
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
