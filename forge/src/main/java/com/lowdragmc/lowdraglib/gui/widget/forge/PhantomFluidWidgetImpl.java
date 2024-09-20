package com.lowdragmc.lowdraglib.gui.widget.forge;

import net.minecraftforge.fluids.FluidStack;

public class PhantomFluidWidgetImpl {
    public static Object checkJEIIngredient(Object ingredient) {
        if (ingredient instanceof FluidStack fluidStack) {
            return com.lowdragmc.lowdraglib.side.fluid.FluidStack.create(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
        }
        return ingredient;
    }
}
