package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.block;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@LDLRegister(name = "blockentity info", group = "graph_processor.node.minecraft.block")
public class BlockEntityInfoNode extends BaseNode {
    @InputPort
    public Object in;
    @OutputPort
    public Level level;
    @OutputPort
    public BlockPos pos;
    @OutputPort(name = "blockstate")
    public BlockState blockState;
    @OutputPort
    public CompoundTag tag;

    @Override
    public void process() {
        if (in instanceof BlockEntity be) {
            level = be.getLevel();
            pos = be.getBlockPos();
            blockState = be.getBlockState();
            tag = be.saveWithId();
        }
    }
}
