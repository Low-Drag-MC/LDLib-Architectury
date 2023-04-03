package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;

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
    protected List<EmiIngredient> inputs;
    protected List<EmiStack> outputs;
    protected int width, height;

    public ModularEmiRecipe(Supplier<T> widget) {
        this.widget = widget;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        var widgetT = widget.get();
        this.width = widgetT.getSize().width;
        this.height = widgetT.getSize().height;

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widgetT);
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                Object ingredient = slot.getJEIIngredient();
                if (ingredient instanceof EmiIngredient emiIngredient) {
                    if (io == IngredientIO.INPUT) {
                        inputs.add(emiIngredient);
                    } else if (io == IngredientIO.OUTPUT) {
                        outputs.addAll(emiIngredient.getEmiStacks());
                    }
                }
            }
        }
    }

    public List<Widget> getFlatWidgetCollection(T widgetIn) {
        List<Widget> widgetList = new ArrayList<>();
        if (widgetIn instanceof WidgetGroup group) {
            for (Widget widget : group.widgets) {
                widgetList.add(widget);
                if (widget instanceof WidgetGroup) {
                    widgetList.addAll(((WidgetGroup) widget).getContainedWidgets(true));
                }
            }
        } else {
            widgetList.add(widgetIn);
        }
        return widgetList;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
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

        widgets.add(new ModularWrapperWidget(modular));
        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widget);
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                if (slot.getJEIIngredient() instanceof EmiIngredient ingredient && (io == IngredientIO.INPUT || io == IngredientIO.OUTPUT)) {
                    widgets.add(new ModularSlotWidget(ingredient,
                            new Bounds(w.getPosition().x, w.getPosition().y, w.getSize().width, w.getSize().height),
                            this));
                }
            }
        }
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
        widgets.add(new ModularWrapperWidget(modular));
        TEMP_CACHE = modular;
    }
}
