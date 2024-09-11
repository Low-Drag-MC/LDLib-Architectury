package com.lowdragmc.lowdraglib.gui.graphprocessor.data.custom;

import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.NodePort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortEdge;

import javax.annotation.Nullable;
import java.util.List;

public interface ICustomPortIODelegate {
    /**
     * Push / pull the data from the input port to the output port.
     */
    void handle(BaseNode node, List<PortEdge> edges, NodePort outputPort);
}
