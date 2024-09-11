package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.conditional;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "greater", group = "graph_processor.node.conditional")
public class GreaterNode extends BaseNode {
    @InputPort
    public float a = 0;
    @InputPort
    public float b = 0;
    @OutputPort
    public boolean out;

    @Override
    public void process() {
        out = a > b;
    }
}
