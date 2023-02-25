package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public interface IManaged {

    /**
     * Get the sync field holder, usually a static field.
     */
    ManagedFieldHolder getFieldHolder();

    /**
     * Get managed storage.
     */
    IManagedStorage getSyncStorage();

    /**
     * on field updated
     */
    void onChanged();

    /**
     * add a listener to field update
     *
     * @param <T>      field type;
     * @param name     managed key
     * @param listener listener
     * @return callback that you can unsubscribe
     */
    default <T> ISubscription addSyncUpdateListener(String name, IFieldUpdateListener<T> listener) {
        return getSyncStorage().addSyncUpdateListener(getFieldHolder().getSyncedFieldIndex(name), listener);
    }

    /**
     * Marks a field as changed, so it will be synced.
     *
     * @param name the key of the field, usually its name
     */
    default void markDirty(String name) {
        getSyncStorage().getFieldByKey(getFieldHolder().getSyncedFieldIndex(name)).setChanged(true);
    }

}
