package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.ManagedFieldUtils;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IManagedBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ManagedFieldStorage {

    private class FieldUpdateSubscription implements IAutoSyncBlockEntity.Subscription {
        @NotNull
        final ManagedKey key;
        @NotNull
        final IAutoSyncBlockEntity.FieldUpdateListener<?> listener;

        public FieldUpdateSubscription(@NotNull ManagedKey key, IAutoSyncBlockEntity.@NotNull FieldUpdateListener<?> listener) {
            this.key = key;
            this.listener = listener;
        }

        @Override
        public void unsubscribe() {
            ManagedFieldStorage.this.listeners.getOrDefault(key, new ArrayList<>()).remove(this);
        }
    }

    private final IManagedBlockEntity blockEntity;

    private BitSet dirtyFields;

    private IRef[] syncFields;

    private boolean initialized = false;
    private IRef[] persistedFields;
    private IRef[] nonLazyFields;
    private Map<ManagedKey, IRef> fieldMap;

    private ReentrantLock lock = new ReentrantLock();


    private final Map<ManagedKey, List<FieldUpdateSubscription>> listeners = new HashMap<>();

    public <T> IAutoSyncBlockEntity.Subscription addSyncUpdateListener(ManagedKey key, IAutoSyncBlockEntity.FieldUpdateListener<T> listener) {
        var subscription = new FieldUpdateSubscription(key, listener);
        listeners.computeIfAbsent(key, k -> new ArrayList<>()).add(subscription);
        return subscription;
    }

    public void removeAllSyncUpdateListener(ManagedKey key) {
        listeners.remove(key);
    }

    public boolean hasSyncListener(ManagedKey key) {
        var list = listeners.get(key);
        return list != null && !list.isEmpty();
    }

    public <T> void notifyFieldUpdate(ManagedKey key, T newVal, T oldVal) {
        var list = listeners.get(key);
        if (list != null) {
            for (var sub : list) {
                //noinspection unchecked
                var listener = (IAutoSyncBlockEntity.FieldUpdateListener<T>) sub.listener;
                try {
                    listener.onFieldChanged(key.getName(), newVal, oldVal);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private void init() {
        lock.lock();

        try {

            if (initialized) {
                return;
            }
            ManagedKey[] fields = blockEntity.getFieldHolder().getFields();

            var result = ManagedFieldUtils.getFieldRefs(fields, blockEntity, (iS, iP, changed) -> {
                if (dirtyFields != null && iS >= 0) {
                    dirtyFields.set(iS, changed);
                }
                if (changed) {
                    if (iP >= 0) {
                        blockEntity.getSelf().setChanged();
                    }
                }
            });

            syncFields = result.syncedRefs();
            dirtyFields = new BitSet(syncFields.length);
            persistedFields = result.persistedRefs();
            nonLazyFields = result.nonLazyFields();
            fieldMap = result.fieldRefMap();

            initialized = true;

        } finally {
            lock.unlock();
        }

    }

    public ManagedFieldStorage(IAutoSyncBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public IRef[] getSyncFields() {
        init();
        return syncFields;
    }

    @Nullable
    public BitSet getDirtyFields() {
        return dirtyFields;
    }


    public IRef[] getPersistedFields() {
        init();
        return persistedFields;
    }

    public IRef getFieldByKey(ManagedKey key) {
        init();
        return fieldMap.get(key);
    }

    public IRef[] getNonLazyFields() {
        init();
        return nonLazyFields;
    }
}
