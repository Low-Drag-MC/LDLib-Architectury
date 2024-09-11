package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.conditional;

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

@LDLRegister(name = "or", group = "graph_processor.node.conditional")
public class OrNode extends BaseNode {
    @InputPort
    public List<Boolean> inputs = new ArrayList<>();
    @OutputPort
    public boolean out = false;

    @Override
    public void process() {
        out = false;
        if (inputs != null) {
            for (var input : inputs) {
                out |= input;
            }
        }
    }

    @CustomPortBehavior(field = "inputs")
    private List<PortData> listPortBehavior(List<PortEdge> edges) {
        var ports = new ArrayList<PortData>();
        for (int i = 0; i < edges.size() + 1; i++) {
            ports.add(new PortData()
                    .displayName("in " + i)
                    .identifier(String.valueOf(i))
                    .displayType(boolean.class));
        }
        if (inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.clear();
        while (inputs.size() + 1 < ports.size()) {
            inputs.add(true);
        }
        return ports;
    }

    // This function will be called once per port created from the `inputs` custom port function
    // will in parameter the list of the edges connected to this port
    @CustomPortInput(field = "inputs", type = boolean.class)
    private void pullInputs(List<PortEdge> inputEdges, NodePort outputPort) {
        if (inputEdges.isEmpty()) {
            return;
        }
        var value = false;
        for (PortEdge inputEdge : inputEdges) {
            if (inputEdge.passThroughBuffer instanceof Boolean v) {
                value |= v;
            }
        }
        var index = outputPort.owner.getInputPorts().indexOf(outputPort);
        while (inputs.size() <= index) {
            inputs.add(value);
        }
        inputs.set(index, value);
    }
}
