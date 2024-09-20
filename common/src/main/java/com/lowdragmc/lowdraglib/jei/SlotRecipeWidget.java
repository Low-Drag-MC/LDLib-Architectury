package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SlotRecipeWidget implements ISlottedRecipeWidget {

    public final IRecipeIngredientSlot slot;
    public int x, y;

    public SlotRecipeWidget(IRecipeIngredientSlot slot, int x, int y) {
        this.slot = slot;
        this.x = x;
        this.y = y;
    }


    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        if (slot.self().isMouseOverElement(mouseX, mouseY)) {
            return Optional.of(new RecipeSlotUnderMouse(new RecipeSlotDrawable(), getPosition()));
        }
        return Optional.empty();
    }

    /**
     * Get the position of this widget, relative to its parent element.
     * @since 15.10.0
     */
    @Override
    public ScreenPosition getPosition() {
        return new ScreenPosition(0, 0);
    }

    public class RecipeSlotDrawable implements IRecipeSlotDrawable {

        @Override
        public void draw(GuiGraphics guiGraphics) {
            // do nothing
        }

        @Override
        public void drawHoverOverlays(GuiGraphics guiGraphics) {
            // do nothing
        }

        @Override
        public List<Component> getTooltip() {
            return Collections.emptyList();
        }

        @Override
        public void getTooltip(ITooltipBuilder builder) {
            // jei tooltips
            getDisplayedIngredient().ifPresent(ingredient -> getTooltip(builder, ingredient));
        }

        // copied from RecipeSlot.
        private <T> void getTooltip(ITooltipBuilder tooltip, ITypedIngredient<T> typedIngredient) {
            var ingredientManager = JEIPlugin.jeiRuntime.getIngredientManager();

            var ingredientType = typedIngredient.getType();
            var ingredientRenderer = getIngredientRenderer(ingredientType);
            SafeIngredientUtil.getTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient);
            tooltip.addAll(slot.getAdditionalToolTips(new ArrayList<>()));
            addTagNameTooltip(tooltip, ingredientManager, typedIngredient);
            addIngredientsToTooltip(tooltip, typedIngredient);
        }

        private <T> IIngredientRenderer<T> getIngredientRenderer(IIngredientType<T> ingredientType) {
            IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
            return ingredientManager.getIngredientRenderer(ingredientType);
        }

        private <T> void addTagNameTooltip(ITooltipBuilder tooltip, IIngredientManager ingredientManager, ITypedIngredient<T> ingredient) {
            IIngredientType<T> ingredientType = ingredient.getType();
            List<T> ingredients = getIngredients(ingredientType).toList();
            if (ingredients.isEmpty()) {
                return;
            }

            IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
            if (clientConfig.isHideSingleIngredientTagsEnabled() && ingredients.size() == 1) {
                return;
            }

            IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
            ingredientHelper.getTagKeyEquivalent(ingredients)
                    .ifPresent(tagKeyEquivalent -> {
                        tooltip.add(
                                Component.translatable("jei.tooltip.recipe.tag", "")
                                        .withStyle(ChatFormatting.GRAY)
                        );
                        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
                        Component tagName = renderHelper.getName(tagKeyEquivalent);
                        tooltip.add(
                                tagName.copy().withStyle(ChatFormatting.GRAY)
                        );
                    });
        }

        private <T> void addIngredientsToTooltip(ITooltipBuilder tooltip, ITypedIngredient<T> displayed) {
            IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
            if (clientConfig.isTagContentTooltipEnabled()) {
                IIngredientType<T> type = displayed.getType();

                IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
                IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
                IIngredientRenderer<T> renderer = ingredientManager.getIngredientRenderer(type);

                List<T> ingredients = getIngredients(type).toList();

                if (ingredients.size() > 1) {
                    tooltip.add(new TagContentTooltipComponent<>(renderer, ingredients));
                }
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + slot.self().getSizeWidth() && mouseY >= y && mouseY <= y + slot.self().getSizeHeight();
        }

        @Override
        public void setPosition(int x, int y) {
            // do nothing
        }

        @Override
        public IIngredientConsumer createDisplayOverrides() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearDisplayOverrides() {

        }

        @Override
        public Rect2i getRect() {
            return new Rect2i(x, y, slot.self().getSizeWidth(), slot.self().getSizeHeight());
        }

        @Override
        public Stream<ITypedIngredient<?>> getAllIngredients() {
            return slot.getXEIIngredients().stream()
                    .filter(IClickableIngredient.class::isInstance)
                    .map(IClickableIngredient.class::cast)
                    .map(IClickableIngredient::getTypedIngredient);
        }

        @Override
        public Optional<ITypedIngredient<?>> getDisplayedIngredient() {
            var ingredients = slot.getXEIIngredients();
            var index = ingredients.size() > 1 ? Math.abs((int)(System.currentTimeMillis() / 1000) % ingredients.size()) : 0;
            if (ingredients.isEmpty()) return Optional.empty();
            var ingredient = ingredients.get(index);
            if (ingredient instanceof IClickableIngredient<?> clickableIngredient) {
                return Optional.of(clickableIngredient.getTypedIngredient());
            }
            return Optional.empty();
        }

        @Override
        public RecipeIngredientRole getRole() {
            return ModularUIRecipeCategory.mapToRole(slot.getIngredientIO());
        }

        @Override
        public void drawHighlight(GuiGraphics guiGraphics, int color) {
            var rect = getRect();
            int x = rect.getX();
            int y = rect.getY();
            int width = rect.getWidth();
            int height = rect.getHeight();

            guiGraphics.fillGradient(
                    RenderType.guiOverlay(),
                    x,
                    y,
                    x + width,
                    y + height,
                    color,
                    color,
                    0
            );
        }

        @Override
        public Optional<String> getSlotName() {
            return Optional.of(slot.self().getId());
        }
    }
}
