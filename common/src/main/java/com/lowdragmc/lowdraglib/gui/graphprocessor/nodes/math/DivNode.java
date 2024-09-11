package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "div", group = "graph_processor.node.math")
public class DivNode extends BaseNode {
    @InputPort
    public float a = 0;
    @InputPort
    public float b = 0;
    @OutputPort
    public float out = 0;

    @Override
    public void process() {
        out = a / b;
    }
}
