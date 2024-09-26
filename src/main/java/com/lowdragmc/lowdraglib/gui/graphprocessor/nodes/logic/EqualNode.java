package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.logic;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

import java.util.Objects;

@LDLRegister(name = "equal", group = "graph_processor.node.logic")
public class EqualNode extends BaseNode {
    @InputPort
    public Object a;
    @InputPort
    public Object b;
    @OutputPort
    public boolean out;

    @Override
    public void process() {
        out = Objects.equals(a, b);
    }
}
