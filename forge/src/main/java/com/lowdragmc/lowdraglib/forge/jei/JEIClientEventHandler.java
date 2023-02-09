package com.lowdragmc.lowdraglib.forge.jei;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote JEIClientEventHandler
 */
public class JEIClientEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRecipesUpdatedEventEvent(RecipesUpdatedEvent event) {
        JEIPlugin.setupInputHandler();
    }

    @SubscribeEvent
    public static void onMouseClickedEventPre(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseReleasedEventPre(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    ((ModularWrapper<?>) recipe).mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseDragEventPre(ScreenEvent.MouseDragged.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                if (recipeLayout instanceof RecipeLayoutWrapper recipeLayoutWrapper){
                    if (recipeLayoutWrapper.getWrapper().mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
                        recipeLayoutWrapper.onPositionUpdate();
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScrollEventPre(ScreenEvent.MouseScrolled.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                if (recipeLayout instanceof RecipeLayoutWrapper recipeLayoutWrapper){
                    if (recipeLayoutWrapper.getWrapper().mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                        recipeLayoutWrapper.onPositionUpdate();
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardKeyPressedEventPre(ScreenEvent.KeyPressed event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardKeyReleasedEventEventPre(ScreenEvent.KeyReleased event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardCharTypedEventEventPre(ScreenEvent.CharacterTyped event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).charTyped(event.getCodePoint(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
