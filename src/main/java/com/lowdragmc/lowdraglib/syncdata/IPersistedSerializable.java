package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IPersistedSerializable extends INBTSerializable<CompoundTag> {

    @Override
    default CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        PersistedParser.serializeNBT(tag, this.getClass(), this, provider);
        return tag;
    }

    @Override
    default void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        PersistedParser.deserializeNBT(tag, new HashMap<>(), this.getClass(), this, provider);
    }

}
