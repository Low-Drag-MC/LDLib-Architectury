package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularEmiRecipe
 */
public abstract class ModularEmiRecipe<T extends Widget> implements EmiRecipe {
    public static final List<ModularWrapper<?>> CACHE_OPENED = new ArrayList<>();

    protected Supplier<T> widget;
    @Getter
    protected List<EmiIngredient> inputs;
    @Getter
    protected List<EmiStack> outputs;
    @Getter
    protected List<EmiIngredient> catalysts;
    protected int width, height;

    public ModularEmiRecipe(Supplier<T> widgetSupplier) {
        this.widget = widgetSupplier;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.catalysts = new ArrayList<>();
        var widget = widgetSupplier.get();
        this.width = widget.getSize().width;
        this.height = widget.getSize().height;

        List<Pair<WidgetGroup, Widget>> flatVisibleWidgetCollection = getFlatWidgetCollection(widget);
        for (var pair : flatVisibleWidgetCollection) {
            Widget w = pair.getSecond();
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                for (Object ingredient : slot.getXEIIngredients()) {
                    if (ingredient instanceof EmiIngredient emiIngredient) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            inputs.add(emiIngredient);
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            outputs.add(emiIngredient.getEmiStacks().get(0));
                        }
                        if (io == IngredientIO.CATALYST) {
                            catalysts.add(emiIngredient);
                        }
                    }
                }
            }
        }
    }

    public List<Pair<WidgetGroup, Widget>> getFlatWidgetCollection(T widgetIn) {
        List<Pair<WidgetGroup, Widget>> widgetList = new ArrayList<>();
        if (widgetIn instanceof WidgetGroup group) {
            for (Widget widget : group.widgets) {
                widgetList.add(Pair.of(group, widget));
                if (widget instanceof WidgetGroup group1) {
                    widgetList.addAll(group1.getContainedWidgets(true).stream()
                            .map(widget1 -> Pair.of(group1, widget1))
                            .toList());
                }
            }
        } else {
            widgetList.add(Pair.of(null, widgetIn));
        }
        return widgetList;
    }

    @Override
    public int getDisplayWidth() {
        return width;
    }

    @Override
    public int getDisplayHeight() {
        return height;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var widget = this.widget.get();
        var modular = new ModularWrapper<>(widget);
        modular.setRecipeWidget(0, 0);

        synchronized (CACHE_OPENED) {
            CACHE_OPENED.add(modular);
        }
        List<dev.emi.emi.api.widget.Widget> slots = new ArrayList<>();
        for (var pair : getFlatWidgetCollection(widget)) {
            Widget w = pair.getSecond();
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                if (io == IngredientIO.BOTH || io == IngredientIO.INPUT || io == IngredientIO.OUTPUT || io == IngredientIO.CATALYST) {
                    //noinspection unchecked
                    var slotWidget = new SlotWidget(EmiIngredient.of((List<? extends EmiIngredient>) (List<?>) slot.getXEIIngredients()),
                            w.getPosition().x, w.getPosition().y)
                            .customBackground(null, w.getPosition().x, w.getPosition().y, w.getSize().width, w.getSize().height)
                            .drawBack(false);
                    if (io == IngredientIO.CATALYST) {
                        slotWidget.catalyst(true);
                    } else if (io == IngredientIO.OUTPUT) {
                        slotWidget.recipeContext(this);
                    }
                    for (Component component : w.getTooltipTexts()) {
                        slotWidget.appendTooltip(component);
                    }
                    slots.add(slotWidget);

                    // Clear the LDlib slots
                    if (pair.getFirst() != null) {
                        if (slot instanceof com.lowdragmc.lowdraglib.gui.widget.SlotWidget slotW) {
                            slotW.getSlotReference().set(ItemStack.EMPTY);
                            slotW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                            //pair.getFirst().removeWidget(slotW);
                        } else if (slot instanceof TankWidget tankW) {
                            tankW.getFluidTank().setFluid(FluidStack.empty());
                            tankW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                            //pair.getFirst().removeWidget(tankW);
                        }
                    }
                }
            }
        }
        widgets.add(new ModularWrapperWidget(modular, slots));
        slots.forEach(widgets::add);
    }

    public static ModularWrapper<?> TEMP_CACHE = null;
    public void addTempWidgets(WidgetHolder widgets) {
        if (TEMP_CACHE != null) {
            TEMP_CACHE.modularUI.triggerCloseListeners();
            TEMP_CACHE = null;
        }
        var widget = this.widget.get();
        var modular = new ModularWrapper<>(widget);
        modular.setRecipeWidget(0, 0);
        widgets.add(new ModularWrapperWidget(modular, new ArrayList<>()));
        TEMP_CACHE = modular;
    }
}
