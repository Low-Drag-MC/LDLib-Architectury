package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface IRef {

    /**
     * ManagedKey refer to ref's meta info.
     */
    ManagedKey getKey();

    /**
     * whether it has changed.
     */
    boolean isChanged();

    /**
     * mark it as changed or not.
     */
    void setChanged(boolean changed);

    /**
     * called to automatically check its internal changed per tick.
     */
    default void update() {
    }

    /**
     * listener should be called while it has changed.
     */
    void setChangeListener(BooleanConsumer listener);

    /**
     * is a lazy ref
     */
    boolean isLazy();

    <T> T readRaw();

}
