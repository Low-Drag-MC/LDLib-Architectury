package com.lowdragmc.lowdraglib.syncdata.managed;

import com.google.common.base.Strings;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import javax.annotation.Nullable;

public interface IRef {
    /**
     * ManagedKey refer to ref's meta info.
     */
    ManagedKey getKey();

    /**
     * whether it is dirty.
     */
    boolean isSyncDirty();

    /**
     * whether it is dirty.
     */
    boolean isPersistedDirty();

    /**
     * clear dirty mark.
     */
    void clearSyncDirty();

    /**
     * clear dirty mark.
     */
    void clearPersistedDirty();

    /**
     * mark it as dirty.
     */
    void markAsDirty();

    /**
     * called to automatically check its internal changed per tick.
     */
    default void update() {
    }

    /**
     * listener should be called while it has changed.
     */
    void setOnSyncListener(BooleanConsumer listener);

    /**
     * listener should be called while it has changed.
     */
    void setOnPersistedListener(BooleanConsumer listener);

    /**
     * is a lazy ref
     */
    boolean isLazy();

    <T> T readRaw();

    /**
     * set persisted prefix name
     */
    @Nullable
    String getPersistedPrefixName();

    /**
     * set persisted prefix name
     */
    void setPersistedPrefixName(String name);

    default String getPersistedKey() {
        var fieldKey = getKey();
        String key = fieldKey.getPersistentKey();
        if (Strings.isNullOrEmpty(key)) {
            key = fieldKey.getName();
        }
        if (!Strings.isNullOrEmpty(getPersistedPrefixName())) {
            key = getPersistedPrefixName() + "." + key;
        }
        return key;
    }

}
