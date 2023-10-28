package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

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

}
