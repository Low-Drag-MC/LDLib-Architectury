package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.trigger;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@LDLRegister(name = "spawn entity", group = "graph_processor.node.minecraft")
public class SpawnEntityNode extends LinearTriggerNode {
    @InputPort
    public Level level;
    @InputPort
    public Vector3f xyz;
    @InputPort(name = "entity type")
    public EntityType entityType;
    @InputPort
    public int count;
    @InputPort
    public CompoundTag tag;

    @Override
    public void process() {
        if (level != null && xyz != null && entityType != null) {
            for (int i = 0; i < count; i++) {
                var entity = entityType.create(level);
                if (entity == null) {
                    return;
                }
                if (tag != null) {
                    entity.load(tag);
                }
                entity.setPos(xyz.x, xyz.y, xyz.z);
                level.addFreshEntity(entity);
            }
        }
    }
}
