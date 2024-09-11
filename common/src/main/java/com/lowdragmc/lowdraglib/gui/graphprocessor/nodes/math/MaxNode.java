package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortBehavior;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortInput;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.NodePort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortData;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortEdge;

import java.util.ArrayList;
import java.util.List;

@LDLRegister(name = "max", group = "graph_processor.node.math")
public class MaxNode extends BaseNode {
    @InputPort
    public List<Float> inputs = new ArrayList<>();
    @OutputPort
    public float out = 0;

    @Override
    public void process() {
        out = -Float.MAX_VALUE;
        if (inputs != null) {
            for (var input : inputs) {
                out = Math.max(out, input);
            }
        } else {
            out = 0;
        }
    }

    @CustomPortBehavior(field = "inputs")
    private List<PortData> listPortBehavior(List<PortEdge> edges) {
        var ports = new ArrayList<PortData>();
        for (int i = 0; i < edges.size() + 1; i++) {
            ports.add(new PortData()
                    .displayName("in " + i)
                    .identifier(String.valueOf(i))
                    .displayType(float.class));
        }
        if (inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.clear();
        while (inputs.size() + 1 < ports.size()) {
            inputs.add(0f);
        }
        return ports;
    }

    // This function will be called once per port created from the `inputs` custom port function
    // will in parameter the list of the edges connected to this port
    @CustomPortInput(field = "inputs", type = float.class)
    private void pullInputs(List<PortEdge> inputEdges, NodePort outputPort) {
        if (inputEdges.isEmpty()) {
            return;
        }
        var value = -Float.MAX_VALUE;
        for (PortEdge inputEdge : inputEdges) {
            if (inputEdge.passThroughBuffer instanceof Number v) {
                value = Math.max(value, v.floatValue());
            }
        }
        var index = outputPort.owner.getInputPorts().indexOf(outputPort);
        while (inputs.size() <= index) {
            inputs.add(0f);
        }
        inputs.set(index, value);
    }

}
