package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.google.common.base.Strings;
import com.lowdragmc.lowdraglib.utils.TagUtils;
import net.minecraft.nbt.CompoundTag;

public interface IAutoPersistBlockEntity extends IManagedBlockEntity {

    default void saveManagedPersistentData(CompoundTag tag, boolean forDrop) {
        var persistedFields = getSyncStorage().getPersistedFields();

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
        var refs = getSyncStorage().getPersistedFields();

        for (var ref : refs) {
            var fieldKey = ref.getKey();
            String key = fieldKey.getPersistentKey();
            if (Strings.isNullOrEmpty(key)) {
                key = fieldKey.getName();
            }

            var nbt = TagUtils.getTagExtended(tag, key);
            if (nbt != null) {
                fieldKey.writePersistedField(ref, nbt);
            }

        }

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
