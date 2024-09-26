package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "random", group = "graph_processor.node.math")
public class RandomNode extends BaseNode {
    @InputPort
    public float min = 0;
    @InputPort
    public float max = 0;
    @OutputPort
    public float out = 0;

    @Override
    public void process() {
        if (max == min) {
            out = min;
            return;
        }
        out = LDLib.random.nextFloat(Math.min(min, max), Math.max(min, max));
    }
}
