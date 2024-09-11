package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "number", group = "graph_processor.node.base")
public class NumberNode extends BaseNode {
    @InputPort
    @Configurable(showName = false)
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 1f)
    @DefaultValue(numberValue = {0})
    public float in = 0;
    @OutputPort
    public float out = 10;

    @Override
    public void process() {
        out = in;
    }
}
