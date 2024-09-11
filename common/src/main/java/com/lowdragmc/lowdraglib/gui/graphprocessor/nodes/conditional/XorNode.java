package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.conditional;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "xor", group = "graph_processor.node.conditional")
public class XorNode extends BaseNode {
    @InputPort
    public boolean a = false;
    @InputPort
    public boolean b = false;
    @OutputPort
    public boolean out = false;

    @Override
    public void process() {
        out = a ^ b;
    }
}
