package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import org.joml.Vector3f;

@LDLRegister(name = "vector3", group = "graph_processor.node.base")
public class Vector3Node extends BaseNode {
    @InputPort(name = "xyz")
    @Configurable(showName = false)
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 1f)
    public Vector3f in;
    @OutputPort(name = "xyz")
    public Vector3f out;
    @InputPort(name = "x")
    public Float inX;
    @InputPort(name = "y")
    public Float inY;
    @InputPort(name = "z")
    public Float inZ;
    @OutputPort(name = "x")
    public float outX;
    @OutputPort(name = "y")
    public float outY;
    @OutputPort(name = "z")
    public float outZ;

    @Override
    public void process() {
        out = new Vector3f(in == null ? new Vector3f() : in);
        out = new Vector3f(
                inX == null ? out.x() : inX,
                inY == null ? out.y() : inY,
                inZ == null ? out.z() : inZ);
        outX = out.x();
        outY = out.y();
        outZ = out.z();
    }

    @Override
    public int getMinWidth() {
        return 150;
    }
}
