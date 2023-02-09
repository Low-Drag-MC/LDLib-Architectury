package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldStorage;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;

public interface IManaged {

    default IRef[] getNonLazyFields() {
        return getSyncStorage().getNonLazyFields();
    }

    default void defaultServerTick() {
        for (IRef field : getNonLazyFields()) {
            field.update();
        }
    }

    /**
     * Get the sync field holder, usually a static field.
     */
    ManagedFieldHolder getFieldHolder();


    /**
     * Get the field storage
     */
    ManagedFieldStorage getSyncStorage();
}
