package com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;

@LDLRegister(name = "trigger start", group = "graph_processor.node.logic")
public class StartNode extends BaseNode implements ITriggerableNode {
    @OutputPort(name = "Triggered", priority = -10000)
    public TriggerLink triggered;
}
