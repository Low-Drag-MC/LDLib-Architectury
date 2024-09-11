package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "bool", group = "graph_processor.node.base")
public class BoolNode extends BaseNode {
    @InputPort
    @Configurable(name = " ")
    public boolean in = false;
    @OutputPort
    public boolean out = false;

    @Override
    public void process() {
        out = in;
    }
}
