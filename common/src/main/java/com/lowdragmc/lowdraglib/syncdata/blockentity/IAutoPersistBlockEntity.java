package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.google.common.base.Strings;
import com.lowdragmc.lowdraglib.syncdata.accessor.IManagedAccessor;
import com.lowdragmc.lowdraglib.utils.TagUtils;
import net.minecraft.nbt.CompoundTag;

public interface IAutoPersistBlockEntity extends IManagedBlockEntity {

    default void saveManagedPersistentData(CompoundTag tag, boolean forDrop) {
        var persistedFields = getRootStorage().getPersistedFields();

        for (var persistedField : persistedFields) {
            var fieldKey = persistedField.getKey();

            if (forDrop && !fieldKey.isDrop()) {
                continue;
            }

            String key = fieldKey.getPersistentKey();
            if (Strings.isNullOrEmpty(key)) {
                key = fieldKey.getName();
            }
            var nbt = fieldKey.readPersistedField(persistedField);
            if (nbt != null) {
                TagUtils.setTagExtended(tag, key, nbt);
            }
        }

        saveCustomPersistedData(tag, forDrop);
    }

    default void loadManagedPersistentData(CompoundTag tag) {
        var refs = getRootStorage().getPersistedFields();
        IManagedAccessor.writePersistedFields(tag, refs);
        loadCustomPersistedData(tag);
    }


    /**
     * write custom data to the save
     */
    default void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {

    }

    /**
     * read custom data from the save
     */
    default void loadCustomPersistedData(CompoundTag tag) {
    }


}
