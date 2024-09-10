package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.BlockPos;

@LDLRegister(name = "blockpos", group = "graph_processor.node")
public class BlockPosNode extends BaseNode {
    @InputPort
    public int x = 0;
    @InputPort
    public int y = 0;
    @InputPort
    public int z = 0;
    @OutputPort
    public BlockPos output = BlockPos.ZERO;
}
