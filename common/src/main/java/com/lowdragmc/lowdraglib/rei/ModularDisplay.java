package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

        for (Widget w : getFlatWidgetCollection(widget.get())) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIO();
                for (Object ingredient : slot.getXEIIngredients()) {
                    if (ingredient instanceof EntryStack<?> entryType) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            inputs.add(EntryIngredient.of(entryType));
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            outputs.add(EntryIngredient.of(entryType));
                        }
                        if (io == IngredientIO.CATALYST) {
                            catalysts.add(EntryIngredient.of(entryType));
                        }
                    } else if (ingredient instanceof EntryIngredient entryStacks) {
                        if (io == IngredientIO.INPUT || io == IngredientIO.BOTH) {
                            inputs.add(entryStacks);
                        }
                        if (io == IngredientIO.OUTPUT || io == IngredientIO.BOTH) {
                            outputs.add(entryStacks);
                        }
                        if (io == IngredientIO.CATALYST) {
                            catalysts.add(entryStacks);
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

    @Environment(EnvType.CLIENT)
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

        for (Widget w : getFlatWidgetCollection(widget)) {
            if (w instanceof IRecipeIngredientSlot slot) {
                EntryWidget entryWidget = new EntryWidget(new Rectangle(slot.self().getPosition().x, slot.self().getPosition().y, slot.self().getSize().width, slot.self().getSize().height))
                        .noBackground();
                if (slot.getIngredientIO() == IngredientIO.INPUT) {
                    entryWidget.markIsInput();
                } else if (slot.getIngredientIO() == IngredientIO.OUTPUT) {
                    entryWidget.markIsOutput();
                } else {
                    entryWidget.unmarkInputOrOutput();
                }
                list.add(entryWidget);
                for (Object ingredient : slot.getXEIIngredients()) {
                    if (ingredient instanceof EntryStack<?> entryStack) {
                        entryWidget.entry(entryStack);
                    } else if (ingredient instanceof EntryIngredient entryStacks) {
                        entryWidget.entries(entryStacks);
                    }
                }

                // Clear the LDlib slots
                if (slot instanceof com.lowdragmc.lowdraglib.gui.widget.SlotWidget slotW) {
                    slotW.setHandlerSlot(IItemTransfer.EMPTY, 0);
                    slotW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                } else if (slot instanceof TankWidget tankW) {
                    tankW.setFluidTank(IFluidStorage.EMPTY);
                    tankW.setDrawHoverOverlay(false).setDrawHoverTips(false);
                }
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
