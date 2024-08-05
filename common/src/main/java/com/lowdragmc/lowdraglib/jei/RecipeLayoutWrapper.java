package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.core.mixins.jei.RecipeLayoutAccessor;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.gui.recipes.ShapelessIcon;
import mezz.jei.library.gui.recipes.layout.builder.RecipeLayoutBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * To reduce workload and allow for customization, we wrapped and expanded {@link RecipeLayout}  to fit our needs.
 */
public class RecipeLayoutWrapper<R> extends RecipeLayout<R> {

    private static final int RECIPE_BORDER_PADDING = 4;

    private final RecipeLayoutAccessor accessor = (RecipeLayoutAccessor) this;

    /**
     * LDLib wraps the recipe inside ModularWrapper so that we can control the rendering of the recipe ourselves.
     */
    @Getter
    private final ModularWrapper<?> wrapper;


    public static <T> RecipeLayout<T> createWrapper(
            IRecipeCategory<T> recipeCategory,
            Collection<IRecipeCategoryDecorator<T>> decorators,
            T recipe,
            IFocusGroup focuses,
            IIngredientManager ingredientManager,
            IScalableDrawable recipeBackground,
            int recipeBorderPadding
    ) {
        RecipeLayoutWrapper<T> wrapper = new RecipeLayoutWrapper<>(recipeCategory, decorators, recipe, recipeBackground, recipeBorderPadding);
        if (wrapper.setRecipeLayout(recipeCategory, recipe, focuses, ingredientManager)) {
            return wrapper;
        }
        return null;
    }

    @Override
    public void setPosition(int posX, int posY) {
        super.setPosition(posX, posY);
        var recipe = getWrapper();
        recipe.setRecipeLayout(posX, posY);
        List<IRecipeSlotDrawable> recipeSlots = new ArrayList<>();
        List<Widget> allWidgets = recipe.modularUI.getFlatWidgetCollection();
        for (var slot : accessor.getAllSlots()) {
            if (slot instanceof RecipeSlotWrapper slotWrapper) {
                recipeSlots.add(slotWrapper);
                continue;
            }
            var rect = slot.getRect();
            Widget widget = allWidgets.get(rect.getX());
            Position position = widget.getPosition();
            recipeSlots.add(new RecipeSlotWrapper(widget, slot, position.x - posX, position.y - posY));
        }
        String uid = recipe.getUid();
        if (uid != null) this.addOutputTooltips(recipeSlots, uid);
        this.setRecipeSlots(recipeSlots);
    }

    private boolean setRecipeLayout(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            IFocusGroup focuses,
            IIngredientManager ingredientManager
    ) {
        RecipeLayoutBuilder<R> builder = new RecipeLayoutBuilder<>(recipeCategory, recipe, ingredientManager);
        try {
            recipeCategory.setRecipe(builder, recipe, focuses);
            if (!builder.isEmpty()) {
                builder.buildRecipeLayout(focuses,
                        Collections.emptyList(),
                        accessor.getRecipeBackground(),
                        accessor.getRecipeBorderPadding());
                return true;
            }
        } catch (RuntimeException | LinkageError e) {
            accessor.getLogger().error("Error caught from Recipe Category: {}", recipeCategory.getRecipeType().getUid(), e);
        }
        return false;
    }

    private void setRecipeSlots(List<IRecipeSlotDrawable> recipeSlots) {
        accessor.getAllSlots().clear();
        accessor.getAllSlots().addAll(recipeSlots);
    }

    public RecipeLayoutWrapper(
            IRecipeCategory<R> recipeCategory,
            Collection<IRecipeCategoryDecorator<R>> decorators,
            R recipe,
            IScalableDrawable recipeBackground,
            int recipeBorderPadding
    ) {
        super(recipeCategory, decorators, recipe, recipeBackground, recipeBorderPadding,
                null, new ImmutablePoint2i(
                        recipeCategory.getWidth() + recipeBorderPadding + RecipeLayout.RECIPE_BUTTON_SPACING,
                        recipeCategory.getHeight() + recipeBorderPadding - RecipeLayout.RECIPE_BUTTON_SIZE
        ), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        this.wrapper = (ModularWrapper<?>) recipe;
    }

    /**
     * Rewrite the rendering of the recipe to use the LDLib wrapped rendering method.
     */
    @Override
    public void drawRecipe(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        IRecipeCategory<R> recipeCategory = getRecipeCategory();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        accessor.getRecipeBackground().draw(graphics, getRectWithBorder());
        ImmutableRect2i area = accessor.getArea();
        int posX = area.getX();
        int posY = area.getY();
        final int recipeMouseX = mouseX - posX;
        final int recipeMouseY = mouseY - posY;
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(posX, posY, 0);
            poseStack.pushPose();
            {
                recipeCategory.draw(getRecipe(), getRecipeSlotsView(), graphics, recipeMouseX, recipeMouseY);

                // drawExtras and drawInfo often render text which messes with the color, this clears it
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            poseStack.popPose();

            // defensive push/pop to protect against recipe categories changing the last pose
            graphics.pose().pushPose();
            {
                wrapper.draw(graphics, recipeMouseX, recipeMouseY, Minecraft.getInstance().getDeltaFrameTime());
                //drawExtras and drawInfo often render text which messes with the color, this clears it
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            graphics.pose().popPose();

            //We have any shapeless recipes?
            ShapelessIcon shapelessIcon = accessor.getShapelessIcon();
            if (shapelessIcon != null) {
                shapelessIcon.draw(graphics);
            }

        }
        graphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    @Override
    public void drawOverlays(@NotNull @Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        //:P
        if (wrapper.tooltipTexts != null && !wrapper.tooltipTexts.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 240);
            graphics.renderTooltip(Minecraft.getInstance().font, wrapper.tooltipTexts, Optional.ofNullable(wrapper.tooltipComponent), mouseX, mouseY);
            graphics.pose().popPose();
        }
    }

    /**
     * Sync slots position.
     */
    public void onPositionUpdate() {
        accessor.getAllSlots()
                .stream()
                .filter(RecipeSlotWrapper.class::isInstance)
                .map(RecipeSlotWrapper.class::cast)
                .forEach(slotWrapper -> slotWrapper.onPositionUpdate(this));
    }

    private void addOutputTooltips(List<IRecipeSlotDrawable> recipeSlots, String uid) {
        List<IRecipeSlotDrawable> outputSlots = recipeSlots.stream()
                .filter(r -> r.getRole() == RecipeIngredientRole.OUTPUT)
                .toList();
        if (outputSlots.isEmpty()) return;

        for (IRecipeSlotDrawable outputSlot : outputSlots) {
            outputSlot.addTooltipCallback(new RegisterNameTooltipCallback(uid));
        }
    }

    private static class RegisterNameTooltipCallback implements IRecipeSlotTooltipCallback {

        private final ResourceLocation uid;
        private final IModIdHelper modIdHelper;

        private RegisterNameTooltipCallback(String uid) {
            this.uid = new ResourceLocation(uid);
            this.modIdHelper = Internal.getJeiRuntime().getJeiHelpers().getModIdHelper();
        }

        @Override
        public void onTooltip(@NotNull IRecipeSlotView recipeSlotView, @NotNull List<Component> tooltip) {
            if (recipeSlotView.getRole() != RecipeIngredientRole.OUTPUT) return;
            if (modIdHelper.isDisplayingModNameEnabled()) {
                String modName = modIdHelper.getFormattedModNameForModId(uid.getNamespace());
                var recipeBy = Component.translatable("jei.tooltip.recipe.by", modName);
                tooltip.add(recipeBy.withStyle(ChatFormatting.GRAY));
            }

            Minecraft minecraft = Minecraft.getInstance();
            boolean showAdvanced = minecraft.options.advancedItemTooltips || Screen.hasShiftDown();
            if (showAdvanced) {
                String recipeUid = ResourceLocation.DEFAULT_NAMESPACE.equals(uid.getNamespace()) ? uid.getPath() : uid.toString();
                var recipeId = Component.translatable("jei.tooltip.recipe.id", recipeUid);
                tooltip.add(recipeId.withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

}
