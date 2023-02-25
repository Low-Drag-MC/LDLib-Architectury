package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.IFieldUpdateListener;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author KilaBash
 * @date 2023/2/17
 * @implNote FieldUpdateSubscription
 */
public abstract class FieldUpdateSubscription implements ISubscription {
    @NotNull
    public final ManagedKey key;
    @NotNull
    public final IFieldUpdateListener<?> listener;

    public FieldUpdateSubscription(@NotNull ManagedKey key, @NotNull IFieldUpdateListener<?> listener) {
        this.key = key;
        this.listener = listener;
    }

    @Override
    abstract public void unsubscribe();
}
