package com.lowdragmc.lowdraglib.gui.widget.forge;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.common.input.ClickableIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.ingredients.TypedIngredient;

/**
 * @author KilaBash
 * @date 2023/6/8
 * @implNote TankWidgetImpl
 */
public class TankWidgetImpl {
    public static Object getPlatformFluidTypeForJEI(FluidStack fluidStack, Position pos, Size size) {
        return new ClickableIngredient<>(TypedIngredient.createUnvalidated(ForgeTypes.FLUID_STACK, new net.minecraftforge.fluids.FluidStack(fluidStack.getFluid(), (int) fluidStack.getAmount(), fluidStack.getTag())),
                new ImmutableRect2i(pos.x, pos.y, size.width, size.height));
    }
}
