package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.logic;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.TriggerNode;

@LDLRegister(name = "select", group = "graph_processor.node.logic")
public class SelectNode extends TriggerNode {
    @InputPort
    public Object a;
    @InputPort
    public Object b;
    @InputPort
    public boolean condition;
    @OutputPort
    public Object out;

    @Override
    public void process() {
        out = condition ? a : b;
    }
}
