package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

@LDLRegister(name = "compound", group = "graph_processor.node.minecraft")
public class CompoundNode extends BaseNode {
    @InputPort
    public Object in = null;
    @OutputPort
    public CompoundTag out = null;
    @Configurable(showName = false)
    public CompoundTag internalValue = new CompoundTag();

    @Override
    public int getMinWidth() {
        return 120;
    }

    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        } else if (in instanceof CompoundTag compoundTag) {
            out = compoundTag;
        } else {
            try {
                out = TagParser.parseTag(in.toString());
            } catch (CommandSyntaxException e) {
                out = null;
            }
        }
        internalValue = out;
    }
}
