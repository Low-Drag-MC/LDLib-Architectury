package com.lowdragmc.lowdraglib.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

@Getter
public class EntityInfo {
    @Setter
    private int id;
    @Nullable
    @Setter
    private CompoundTag tag;
    @Nullable
    @Setter
    private EntityType<?> entityType;

    public EntityInfo(int id) {
        this.id = id;
    }

    public EntityInfo(int id, @Nullable EntityType<?> entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    public EntityInfo(int id, @Nullable EntityType<?> entityType, @Nullable CompoundTag tag) {
        this.id = id;
        this.entityType = entityType;
        this.tag = tag;
    }

}
