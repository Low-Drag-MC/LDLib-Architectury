package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.value;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.utils.PrintNode;

import java.util.Objects;

@LDLRegister(name = "string", group = "graph_processor.node.value")
public class StringNode extends BaseNode {
    @InputPort
    public Object in;
    @OutputPort
    public String out;

    @Configurable(showName = false)
    private String internalValue;

    @Override
    public void process() {
        if (in == null) {
            out = Objects.requireNonNullElse(internalValue, "");
        } else {
            internalValue = PrintNode.format(in);
            out = internalValue;
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
