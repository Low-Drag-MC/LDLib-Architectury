package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.gui.editor.ILDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.ILDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;

/**
 * @author KilaBash
 * @date 2023/6/3
 * @implNote IAutoPersistedSerializable
 */
public interface IAutoPersistedSerializable extends INBTSerializable<CompoundTag> {
    @Override
    default CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        PersistedParser.serializeNBT(tag, this.getClass(), this);
        if (this instanceof ILDLRegisterClient registerClient) {
            tag.putString("_type", registerClient.name());
        } else if (this instanceof ILDLRegister register) {
            tag.putString("_type", register.name());
        }
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
        PersistedParser.deserializeNBT(tag, new HashMap<>(), this.getClass(), this);
    }

}
