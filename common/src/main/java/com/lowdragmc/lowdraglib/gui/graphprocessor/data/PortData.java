package com.lowdragmc.lowdraglib.gui.graphprocessor.data;

import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Class that describe port attributes for it's creation
 */
@Setter
@Accessors(chain = true, fluent = true)
public class PortData {
    /**
     * Display name on the node
     */
    public String displayName = "";
    /**
     * Unique identifier for the port
     */
    @Nullable
    public String identifier;
    /**
     * he type that will be used for coloring with the type stylesheet
     */
    @Nullable
    public Class displayType;
    /**
     * If the port accept multiple connection
     */
    public boolean acceptMultipleEdges;
//    /**
//     * Port size, will also affect the size of the connected edge
//     */
//    public int sizeInPixel;
    /**
     * Tooltip of the port
     */
    @Nullable
    public List<String> tooltip;
    /**
     * Is the port vertical
     */
    public boolean vertical;

    public PortData() {
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject instanceof PortData  other)
            return Objects.equals(identifier, other.identifier)
                    && Objects.equals(displayName, other.displayName)
                    && displayType == other.displayType
                    && acceptMultipleEdges == other.acceptMultipleEdges
//                && sizeInPixel == other.sizeInPixel
                    && Objects.equals(tooltip, other.tooltip)
                    && vertical == other.vertical;
        return false;
    }

    public void CopyFrom(PortData other) {
        identifier = other.identifier;
        displayName = other.displayName;
        displayType = other.displayType;
        acceptMultipleEdges = other.acceptMultipleEdges;
//        sizeInPixel = other.sizeInPixel;
        tooltip = other.tooltip;
        vertical = other.vertical;
    }
}
