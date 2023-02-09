package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface IRef {
    ManagedKey getKey();
    boolean isChanged();

    void setChanged(boolean changed);

    default void update() {
    }

    void setChangeListener(BooleanConsumer listener);
    boolean isLazy();

    <T> T readRaw();
}
