package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.util.Mth;

@LDLRegister(name = "random", group = "graph_processor.node.util")
public class RandomNode extends BaseNode {
    @InputPort
    public float min = 0;
    @InputPort
    public float max = 0;
    @OutputPort
    public float out = 0;

    @Override
    public void process() {
        out = LDLib.random.nextFloat(min, max);
    }
}
