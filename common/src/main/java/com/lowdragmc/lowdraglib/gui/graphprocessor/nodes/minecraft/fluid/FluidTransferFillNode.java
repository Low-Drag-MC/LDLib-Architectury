package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.fluid;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.runtime.ConfiguratorParser;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.HashMap;

@LDLRegister(name = "fluid fill", group = "graph_processor.node.minecraft.fluid")
public class FluidTransferFillNode extends LinearTriggerNode {
    @InputPort(name = "fluid transfer")
    public IFluidTransfer fluidTransfer;
    @InputPort
    public FluidStack fluidstack;
    @InputPort
    public Boolean simulate;
    @OutputPort
    public int filled;
    @Configurable(name = "simulate")
    public boolean internalSimulate;

    @Override
    public void process() {
        filled = 0;
        if (fluidTransfer != null && fluidstack != null) {
            filled = (int) fluidTransfer.fill(fluidstack, simulate == null ? internalSimulate : simulate);
        }
    }

    @Override
    @SneakyThrows
    public void buildConfigurator(ConfiguratorGroup father) {
        var setter = new HashMap<String, Method>();
        var clazz = getClass();
        for (var port : getInputPorts()) {
            if (port.fieldName.equals("simulate")) {
                if (port.getEdges().isEmpty()) {
                    ConfiguratorParser.createFieldConfigurator(clazz.getField("internalSimulate"), father, clazz, setter, this);
                }
            }
        }
    }
}
