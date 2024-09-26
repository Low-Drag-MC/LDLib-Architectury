package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.ListMergeNode;

@LDLRegister(name = "min", group = "graph_processor.node.math")
public class MinNode extends ListMergeNode<Float> {

    @Override
    public Class<Float> type() {
        return Float.class;
    }

    @Override
    public Float defaultValue() {
        return Float.MAX_VALUE;
    }

    @Override
    public Float merge(Float a, Float b) {
        return Math.min(a, b);
    }
}
