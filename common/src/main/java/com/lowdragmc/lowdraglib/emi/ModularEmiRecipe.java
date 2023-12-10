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
import lombok.Getter;

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

    public ModularEmiRecipe(Supplier<T> widget) {
        this.widget = widget;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.catalysts = new ArrayList<>();
        var widgetT = widget.get();
        this.width = widgetT.getSize().width;
        this.height = widgetT.getSize().height;

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widgetT);
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                for (Object ingredient : slot.getXEIIngredients()) {
                    if (ingredient instanceof EmiIngredient emiIngredient) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            inputs.add(emiIngredient);
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            outputs.addAll(emiIngredient.getEmiStacks());
                        }
                        if (io == IngredientIO.CATALYST) {
                            catalysts.add(emiIngredient);
                        }
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
        for (Widget w : getFlatWidgetCollection(widget)) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                if (io == IngredientIO.BOTH || io == IngredientIO.INPUT || io == IngredientIO.OUTPUT || io == IngredientIO.CATALYST) {
                    slots.add(new ModularSlotWidget(slot,
                            new Bounds(w.getPosition().x, w.getPosition().y, w.getSize().width, w.getSize().height),
                            this));
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
