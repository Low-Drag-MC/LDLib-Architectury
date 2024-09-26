package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@LDLRegister(name = "entity info", group = "graph_processor.node.minecraft")
public class EntityInfoNode extends BaseNode {
    @InputPort
    public Entity entity;
    @OutputPort(name = "entity type")
    public EntityType entityType;
    @OutputPort(name = "is alive")
    public boolean isAlive;
    @OutputPort
    public Vector3f xyz;
    @OutputPort
    public Level level;

    @Override
    public void process() {
        if (entity != null) {
            entityType = entity.getType();
            isAlive = entity.isAlive();
            xyz = new Vector3f((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
            level = entity.level();
        }
    }
}
