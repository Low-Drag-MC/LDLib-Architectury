package com.lowdragmc.lowdraglib.gui.graphprocessor.data.custom;

import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortData;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortEdge;

import java.util.List;

public interface ICustomPortBehaviorDelegate {
    List<PortData> handle(List<PortEdge> edges);
}
