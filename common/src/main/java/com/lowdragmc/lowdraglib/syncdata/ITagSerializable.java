package com.lowdragmc.lowdraglib.syncdata;

import net.minecraft.nbt.Tag;

public interface ITagSerializable<T extends Tag> {
    T serializeNBT();

    void deserializeNBT(T nbt);
}
