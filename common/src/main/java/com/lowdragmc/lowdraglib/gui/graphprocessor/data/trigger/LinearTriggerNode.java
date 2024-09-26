package com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger;

import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;

public class LinearTriggerNode extends TriggerNode {
    @OutputPort(name = "Triggered", priority = -10000)
    public TriggerLink triggered;
}
