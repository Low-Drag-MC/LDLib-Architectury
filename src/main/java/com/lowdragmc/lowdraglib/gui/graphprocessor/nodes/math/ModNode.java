package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "mod", group = "graph_processor.node.math")
public class ModNode extends BaseNode {
    @InputPort
    public int a = 0;
    @InputPort
    public int b = 0;
    @OutputPort
    public int out = 0;

    @Override
    public void process() {
        out = Math.floorMod(a, b);
    }
}
