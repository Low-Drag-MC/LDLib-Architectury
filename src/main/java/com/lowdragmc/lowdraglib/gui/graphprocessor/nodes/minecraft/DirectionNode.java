package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.Direction;

@LDLRegister(name = "direction", group = "graph_processor.node.minecraft")
public class DirectionNode extends BaseNode {
    @InputPort
    public Object in = null;
    @OutputPort
    public Direction out = null;
    @Configurable(showName = false)
    public Direction internalValue = Direction.NORTH;

    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        } else if (in instanceof Direction direction) {
            out = direction;
        } else if (in instanceof Number number) {
            out = Direction.values()[number.intValue() % Direction.values().length];
        } else {
            try {
                out = Direction.valueOf(in.toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                out = null;
            }
        }
        internalValue = out;
    }
}
