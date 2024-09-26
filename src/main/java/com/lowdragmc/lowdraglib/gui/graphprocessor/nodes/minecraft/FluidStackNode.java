package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

@LDLRegister(name = "fluidstack", group = "graph_processor.node.minecraft")
public class FluidStackNode extends BaseNode {
    @InputPort(name = "fluid")
    public Fluid in;
    @InputPort
    public int count;
    @InputPort
    public DataComponentPatch components;
    @OutputPort(name = "fluidstack")
    public FluidStack out = null;
    @Configurable(name = "fluidstack", canCollapse = false, collapse = false)
    public FluidStack internalValue = FluidStack.create(Fluids.WATER, 1000);

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        }
        out = FluidStack.create(in, count);
        if (components != null) {
            out.getComponents().applyPatch(components);
        }
        internalValue = out;
    }
}
