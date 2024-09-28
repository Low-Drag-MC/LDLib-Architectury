package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.item;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.side.item.ItemTransferHelper;
import com.lowdragmc.lowdraglib.utils.Vector3fHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@LDLRegister(name = "item transfer get", group = "graph_processor.node.minecraft.item")
public class ItemTransferGetNode extends LinearTriggerNode {
    @InputPort
    public Level level;
    @InputPort
    public Vector3f xyz;
    @InputPort(name = "direction")
    public Direction direction;
    @OutputPort(name = "item transfer")
    public IItemTransfer itemTransfer;

    @Override
    public void process() {
        if (level != null && xyz != null) {
            var pos = Vector3fHelper.toBlockPos(xyz);
            itemTransfer = ItemTransferHelper.getItemTransfer(level, pos, direction);
        }
    }
}
