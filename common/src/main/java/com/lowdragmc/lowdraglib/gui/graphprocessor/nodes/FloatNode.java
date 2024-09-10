package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "float", group = "graph_processor.node")
public class FloatNode extends BaseNode {
    @InputPort
    public float input = 0;
    @OutputPort
    public float output = 10;
}
