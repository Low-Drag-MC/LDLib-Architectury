package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.utils;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortBehavior;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortOutput;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.NodePort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortData;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortEdge;

import java.util.List;

@LDLRegister(name = "type cast", group = "graph_processor.node.utils")
public class CastNode extends BaseNode {
    @InputPort
    public Object in;
    @InputPort
    public Class type;
    @OutputPort
    public Object out;

    @Override
    protected void process() {
        if (in == null) {
            out = null;
            return;
        }
        if (type == null) {
            out = in;
            return;
        }
        if (type.isInstance(in)) {
            out = in;
        } else {
            out = null;
        }
    }

    @CustomPortBehavior(field = "out")
    public List<PortData> modifyIfPort(List<PortEdge> edges) {
        return List.of(new PortData()
                .displayName("out")
                .identifier("out")
                .acceptMultipleEdges(true)
                .displayType(type));
    }

    @CustomPortOutput(field = "out")
    public void pushOut(List<PortEdge> outputEdges, NodePort outputPort) {
        for (var edge : outputEdges) {
            edge.passThroughBuffer = out;
        }
    }

}
