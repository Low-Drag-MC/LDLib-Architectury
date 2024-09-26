package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.LDLib;
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

    public EntityType<?> internalValue = EntityType.PIG;

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (in == null) {
            out = internalValue;
            return;
        } else if (in instanceof EntityType entityType) {
            out = entityType;
        } else {
            var name = in.toString();
            if (LDLib.isValidResourceLocation(name)) {
                out = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(name));
            } else {
                out = null;
            }
        }
        internalValue = out;
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        super.buildConfigurator(father);
        var candidates = BuiltInRegistries.ENTITY_TYPE.stream().toList();
        father.addConfigurators(new SelectorConfigurator<>("",
                () -> internalValue,
                type -> internalValue = type,
                EntityType.PIG,
                true,
                candidates,
                type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()));
    }
}
