package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.client.gui.GuiGraphics;

public class ModularSlotEntryWidget extends EntryWidget {

    private final IRecipeIngredientSlot slot;

    public ModularSlotEntryWidget(IRecipeIngredientSlot slot) {
        super(new Rectangle(slot.self().getPosition().x, slot.self().getPosition().y, slot.self().getSize().width, slot.self().getSize().height));
        this.slot = slot;
        if (slot.getIngredientIO() == IngredientIO.INPUT) {
            markIsInput();
        } else if (slot.getIngredientIO() == IngredientIO.OUTPUT) {
            markIsOutput();
        } else {
            unmarkInputOrOutput();
        }
        for (Object ingredient : slot.getXEIIngredients()) {
            if (ingredient instanceof EntryStack<?> entryStack) {
                entry(entryStack);
            } else if (ingredient instanceof EntryIngredient entryStacks) {
                entries(entryStacks);
            }
        }
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void drawBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean containsMouse(double mouseX, double mouseY) {
        return slot.self().isVisible() && slot.self().isMouseOverElement(mouseX, mouseY);
    }

    @Override
    public Rectangle getInnerBounds() {
        var bounds = getBounds();
        return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
    }

}
