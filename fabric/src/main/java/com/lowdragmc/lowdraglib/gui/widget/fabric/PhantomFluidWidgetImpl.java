package com.lowdragmc.lowdraglib.gui.widget.fabric;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;

public class PhantomFluidWidgetImpl {
    public static Object checkJEIIngredient(Object ingredient) {
        if (ingredient instanceof JeiFluidIngredient fluidIngredient) {
            return FluidStack.create(fluidIngredient.getFluid(), fluidIngredient.getAmount(), fluidIngredient.getTag().orElse(null));
        }
        return false;
    }
}
