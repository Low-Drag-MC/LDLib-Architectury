package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.gui.editor.accessors.ItemStackAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@LDLRegister(name = "itemstack", group = "graph_processor.node.minecraft")
public class ItemStackNode extends BaseNode {
    @InputPort(name = "item")
    public Item in;
    @InputPort
    public int count;
    @InputPort
    public CompoundTag tag;
    @OutputPort(name = "itemstack")
    public ItemStack out = null;
    @Configurable(name = "itemstack", canCollapse = false, collapse = false)
    public ItemStack internalValue = new ItemStack(Items.AIR);

    @Override
    public int getMinWidth() {
        return 100;
    }
    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        }
        out = new ItemStack(in, count);
        if (tag != null) {
            out.setTag(tag);
        }
        internalValue = out;
    }
}
