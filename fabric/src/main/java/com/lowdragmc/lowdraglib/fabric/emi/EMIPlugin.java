package com.lowdragmc.lowdraglib.fabric.emi;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.emi.ModularUIEmiRecipeCategory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIEmiHandlers;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.msic.FluidStorage;
import com.lowdragmc.lowdraglib.msic.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote EMIPlugin
 */
public class EMIPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addDragDropHandler(ModularUIGuiContainer.class, ModularUIEmiHandlers.DRAG_DROP_HANDLER);
        registry.addExclusionArea(ModularUIGuiContainer.class, ModularUIEmiHandlers.EXCLUSION_AREA);
        registry.addStackProvider(ModularUIGuiContainer.class, ModularUIEmiHandlers.STACK_PROVIDER);
        var cat = new ModularUIEmiRecipeCategory(new ResourceLocation("ldlib:test_cat"), ItemEmiStack.of(Items.GOLD_INGOT));
        registry.addCategory(cat);
        var recipe = new ModularEmiRecipe<WidgetGroup>(() -> {
            var widgetGroup = new WidgetGroup(0, 0, 120, 40);
            widgetGroup.addWidget(new SlotWidget(new ItemStackTransfer(new ItemStack(Items.STONE)), 0, 0, 0, false, false).setIngredientIO(IngredientIO.INPUT));
            widgetGroup.addWidget(new SlotWidget(new ItemStackTransfer(new ItemStack(Items.GRASS_BLOCK)), 0, 0, 20, false, false).setIngredientIO(IngredientIO.OUTPUT));
            widgetGroup.addWidget(new TankWidget(new FluidStorage(FluidStack.create(Fluids.WATER, 1000)), 20, 0, false, false).setIngredientIO(IngredientIO.INPUT));
            widgetGroup.addWidget(new TankWidget(new FluidStorage(FluidStack.create(Fluids.LAVA, 1000)), 20, 20, false, false).setIngredientIO(IngredientIO.OUTPUT));
            if (LDLib.isRemote()) {
                var entityPlayer = Minecraft.getInstance().player;
                BlockPos pos = entityPlayer.getOnPos();
                SceneWidget sceneWidget = new SceneWidget(50, 0, 40, 40, entityPlayer.level).useCacheBuffer();
                sceneWidget.setRenderedCore(List.of(pos, pos.above(), pos.below(),
                        pos.relative(Direction.NORTH), pos.relative(Direction.SOUTH), pos.relative(Direction.EAST), pos.relative(Direction.WEST)), null);
                widgetGroup.addWidget(sceneWidget);
            }
            return widgetGroup;
        }) {

            @Override
            public EmiRecipeCategory getCategory() {
                return cat;
            }

            @Override
            public ResourceLocation getId() {
                return new ResourceLocation("ldlib:test_recipe");
            }
        };
        registry.addRecipe(recipe);

    }

}
