package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.ListMergeNode;

@LDLRegister(name = "adder", group = "graph_processor.node.math")
public class AdderNode extends ListMergeNode<Float> {

    @Override
    public Class<Float> type() {
        return Float.class;
    }

    @Override
    public Float defaultValue() {
        return 0f;
    }

    @Override
    public Float merge(Float a, Float b) {
        return a + b;
    }
}
