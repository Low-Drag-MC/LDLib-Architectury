package com.lowdragmc.lowdraglib.gui.ingredient;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public interface IRecipeIngredientSlot extends IIngredientSlot {

    default Widget self() {
        return (Widget) this;
    }

    @Nullable
    @Override
    default Object getXEIIngredientOverMouse(double mouseX, double mouseY) {
        if (self().isMouseOverElement(mouseX, mouseY)) {
            var ingredients = getXEIIngredients();
            if (!ingredients.isEmpty()) {
                return ingredients.get(0);
            }
        }
        return null;
    }

    List<Object> getXEIIngredients();

    default float getXEIChance() {
        return 1.0f;
    }

    default IngredientIO getIngredientIO(){
        return IngredientIO.RENDER_ONLY;
    }

    /**
     * Append additional tooltips excluding the ingredient's tooltip.
     */
    default List<Component> getAdditionalToolTips(List<Component> toolTips) {
        return toolTips;
    }

}
