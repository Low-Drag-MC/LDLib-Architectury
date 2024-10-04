package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.data;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

@LDLRegister(name = "direction", group = "graph_processor.node.minecraft.data")
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
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        for (var port : getInputPorts()) {
            if (port.fieldName.equals("in")) {
                if (!port.getEdges().isEmpty()) return;
            }
        }
        super.buildConfigurator(father);
    }
}
