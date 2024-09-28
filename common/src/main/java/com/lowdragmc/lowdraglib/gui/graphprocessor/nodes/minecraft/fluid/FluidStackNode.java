package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.fluid;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

@LDLRegister(name = "fluidstack", group = "graph_processor.node.minecraft.fluid")
public class FluidStackNode extends BaseNode {
    @InputPort
    public Object in;
    @InputPort
    public Fluid fluid;
    @InputPort
    public Integer amount;
    @InputPort
    public CompoundTag tag;
    @OutputPort
    public FluidStack out;
    @Configurable(name = "fluidstack", canCollapse = false, collapse = false)
    public FluidStack internalValue = FluidStack.create(Fluids.WATER, 1000);

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (in == null) {
            out = internalValue.copy();
        } else if (in instanceof FluidStack fluidStack){
            out = fluidStack.copy();
        } else if (in instanceof CompoundTag fluidTag) {
            out = FluidStack.loadFromTag(fluidTag);
        } else {
            out = FluidStack.empty();
        }
        if (fluid != null) {
            var stack = FluidStack.create(fluid, out.getAmount());
            if (out.hasTag()) {
                stack.setTag(out.getTag());
            }
            out = stack;
        }
        if (amount != null) {
            out.setAmount(amount);
        }
        if (tag != null) {
            out.setTag(tag);
        }
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        for (var port : getInputPorts()) {
            if (port.fieldName.equals("in")) {
                if (!port.getEdges().isEmpty()) return;
            }
        }
        super.buildConfigurator(father);
    }
}
