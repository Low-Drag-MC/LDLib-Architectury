package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "color", group = "graph_processor.node.base")
public class ColorNode extends BaseNode {
    @InputPort
    @Configurable(name = " ")
    @NumberColor
    @DefaultValue(numberValue = -1)
    public int in;
    @OutputPort
    public int out;

    @Override
    public void process() {
        out = in;
    }
}
