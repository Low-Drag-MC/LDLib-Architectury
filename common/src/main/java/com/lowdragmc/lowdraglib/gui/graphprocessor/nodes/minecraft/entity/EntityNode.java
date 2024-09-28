package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.entity;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.SelectorConfigurator;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

@LDLRegister(name = "entity type", group = "graph_processor.node.minecraft")
public class EntityNode extends BaseNode {
    @InputPort
    public Object in = null;
    @OutputPort(name = "entity type")
    public EntityType out = null;

    public ResourceLocation internalValue = new ResourceLocation("pig");

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (in == null) {
            out = BuiltInRegistries.ENTITY_TYPE.get(internalValue);
        } else if (in instanceof EntityType entityType) {
            out = entityType;
        } else {
            var name = in.toString();
            if (ResourceLocation.isValidResourceLocation(name)) {
                out = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(name));
            } else {
                out = null;
            }
        }
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        super.buildConfigurator(father);
        for (var port : getInputPorts()) {
            if (port.fieldName.equals("in")) {
                if (!port.getEdges().isEmpty()) return;
            }
        }
        var candidates = BuiltInRegistries.ENTITY_TYPE.keySet().stream().toList();
        father.addConfigurators(new SelectorConfigurator<>("",
                () -> internalValue,
                type -> internalValue = type,
                new ResourceLocation("pig"),
                true,
                candidates,
                ResourceLocation::toString));
    }
}
