package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

@LDLRegister(name = "fluid", group = "graph_processor.node.minecraft")
public class FluidNode extends BaseNode {
    @InputPort
    public Object in = null;
    @OutputPort
    public Fluid out = null;
    @Configurable(showName = false)
    public Fluid internalValue = Fluids.WATER;

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        } else if (in instanceof Fluid fluid) {
            out = fluid;
        } else if (in instanceof FluidStack fluidStack) {
            out = fluidStack.getFluid();
        } else {
            var name = in.toString();
            if (LDLib.isValidResourceLocation(name)) {
                out = BuiltInRegistries.FLUID.get(ResourceLocation.parse(name));
            } else {
                out = null;
            }
        }
        internalValue = out;
    }
}
