package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.item;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.world.item.ItemStack;

@LDLRegister(name = "item transfer info", group = "graph_processor.node.minecraft.item")
public class ItemTransferInfoNode extends BaseNode {
    @InputPort(name = "item transfer")
    public IItemTransfer itemTransfer;
    @InputPort(name = "slot index")
    public Integer slot;
    @OutputPort(name = "slot size")
    public int slots;
    @OutputPort
    public ItemStack itemstack;
    @OutputPort(name = "slot limit")
    public int slotLimit;
    @Configurable(name = "slot index")
    public int internalSlot;

    @Override
    public void process() {
        if (itemTransfer != null) {
            slots = itemTransfer.getSlots();
            var realSlot = slot == null ? internalSlot : slot;
            itemstack = itemTransfer.getStackInSlot(realSlot);
            slotLimit = itemTransfer.getSlotLimit(realSlot);
        }
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        for (var port : getInputPorts()) {
            if (port.fieldName.equals("slot")) {
                if (!port.getEdges().isEmpty()) return;
            }
        }
        super.buildConfigurator(father);
    }
}
