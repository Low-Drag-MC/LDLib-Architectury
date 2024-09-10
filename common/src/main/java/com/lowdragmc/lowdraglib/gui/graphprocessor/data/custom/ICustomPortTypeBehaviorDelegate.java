package com.lowdragmc.lowdraglib.gui.graphprocessor.data.custom;

import com.lowdragmc.lowdraglib.gui.graphprocessor.data.PortData;

import java.util.List;

public interface ICustomPortTypeBehaviorDelegate {
    List<PortData> handle(String fieldName, String displayName, Object value);
}
