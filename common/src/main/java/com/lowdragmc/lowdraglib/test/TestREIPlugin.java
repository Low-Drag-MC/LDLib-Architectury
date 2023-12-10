package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.rei.IGui2Renderer;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;
import com.lowdragmc.lowdraglib.rei.ModularUIDisplayCategory;
import lombok.Getter;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class TestREIPlugin {
    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new TestREIRecipeCategory());
    }

    public static void registerDisplays(DisplayRegistry registry) {
        registry.add(new TestREIRecipeDisplay());
    }

    private static class TestREIRecipeCategory extends ModularUIDisplayCategory<TestREIRecipeDisplay> {
        private static final CategoryIdentifier<TestREIRecipeDisplay> IDENTIFIER = CategoryIdentifier.of(LDLib.MOD_ID, "test_category");

        @Getter
        Renderer icon;

        public TestREIRecipeCategory() {
            icon = IGui2Renderer.toDrawable(new ItemStackTexture(Items.APPLE));
        }

        @Override
        public CategoryIdentifier<TestREIRecipeDisplay> getCategoryIdentifier() {
            return IDENTIFIER;
        }

        @Override
        public Component getTitle() {
            return Component.literal("Test Category");
        }

        @Override
        public int getDisplayWidth(TestREIRecipeDisplay display) {
            return 170;
        }

        @Override
        public int getDisplayHeight() {
            return 60;
        }
    }

    private static class TestREIRecipeDisplay extends ModularDisplay<WidgetGroup> {
        public TestREIRecipeDisplay() {
            super(TestXEIWidgetGroup::new, TestREIRecipeCategory.IDENTIFIER);
        }

    }
}
