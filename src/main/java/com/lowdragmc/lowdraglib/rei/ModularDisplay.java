package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModularDisplay<T extends Widget> implements Display {
    public static final List<ModularWrapper<?>> CACHE_OPENED = new ArrayList<>();
    protected Supplier<T> widget;
    protected List<EntryIngredient> inputs;
    protected List<EntryIngredient> outputs;
    protected List<EntryIngredient> catalysts;
    protected final CategoryIdentifier<?> category;

    public ModularDisplay(Supplier<T> widget, CategoryIdentifier<?> category) {
        this.widget = widget;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.catalysts = new ArrayList<>();
        this.category = category;

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widget.get());
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                for (Object ingredient : slot.getXEIIngredients()) {
                    if (ingredient instanceof EntryStack<?> entryType) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            inputs.add(EntryIngredient.builder().add(entryType).build());
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            outputs.add(EntryIngredient.builder().add(entryType).build());
                        }
                        if (io == IngredientIO.CATALYST) {
                            catalysts.add(EntryIngredient.builder().add(entryType).build());
                        }
                    } else if (ingredient instanceof EntryIngredient entryStacks) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            for (EntryStack<?> entryStack : entryStacks) {
                                inputs.add(EntryIngredient.builder().add(entryStack).build());
                            }
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            for (EntryStack<?> entryStack : entryStacks) {
                                outputs.add(EntryIngredient.builder().add(entryStack).build());
                            }
                        }
                        if (io == IngredientIO.CATALYST) {
                            for (EntryStack<?> entryStack : entryStacks) {
                                catalysts.add(EntryIngredient.builder().add(entryStack).build());
                            }
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

    @OnlyIn(Dist.CLIENT)
    public List<me.shedaniel.rei.api.client.gui.widgets.Widget> createWidget(Rectangle bounds) {
        List<me.shedaniel.rei.api.client.gui.widgets.Widget> list = new ArrayList<>();
        var widget = this.widget.get();
        var modular = new ModularWrapper<>(widget);
        modular.setRecipeWidget(bounds.getX() + 4, bounds.getY() + 4);

        synchronized (CACHE_OPENED) {
            CACHE_OPENED.add(modular);
        }

        list.add(Widgets.createRecipeBase(bounds));
        list.add(new ModularWrapperWidget(modular));

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widget);
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                list.add(new ModularSlotEntryWidget(slot));
            }
        }

        return list;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        var required = new ArrayList<>(catalysts);
        required.addAll(inputs);
        return required;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return category;
    }
}
