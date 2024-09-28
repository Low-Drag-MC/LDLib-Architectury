package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.fluid;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.utils.Vector3fHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@LDLRegister(name = "fluid transfer get", group = "graph_processor.node.minecraft.fluid")
public class FluidTransferGetNode extends LinearTriggerNode {
    @InputPort
    public Level level;
    @InputPort
    public Vector3f xyz;
    @InputPort(name = "direction")
    public Direction direction;
    @OutputPort(name = "fluid transfer")
    public IFluidTransfer fluidTransfer;

    @Override
    public void process() {
        if (level != null && xyz != null) {
            var pos = Vector3fHelper.toBlockPos(xyz);
            fluidTransfer = FluidTransferHelper.getFluidTransfer(level, pos, direction);
        }
    }
}
