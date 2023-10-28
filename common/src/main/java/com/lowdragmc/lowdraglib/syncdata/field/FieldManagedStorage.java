package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.*;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class FieldManagedStorage implements IManagedStorage {

    private final IManaged owner;

    private BitSet dirtySyncFields;
    private BitSet dirtyPersistedFields;

    private IRef[] syncFields;

    private boolean initialized = false;
    private IRef[] persistedFields;
    private IRef[] nonLazyFields;
    private Map<ManagedKey, IRef> fieldMap;

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<ManagedKey, List<FieldUpdateSubscription>> listeners = new HashMap<>();

    public <T> ISubscription addSyncUpdateListener(ManagedKey key, IFieldUpdateListener<T> listener) {
        var subscription = new FieldUpdateSubscription(key, listener) {
            @Override
            public void unsubscribe() {
                listeners.getOrDefault(key, new ArrayList<>()).remove(this);
            }
        };
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
                var listener = (IFieldUpdateListener<T>) sub.listener;
                try {
                    listener.onFieldChanged(key.getName(), newVal, oldVal);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public void init() {
        lock.lock();
        try {
            if (initialized) {
                return;
            }
            ManagedKey[] fields = owner.getFieldHolder().getFields();

            var result = ManagedFieldUtils.getFieldRefs(fields, owner, (ref, index, changed) -> {
                if (dirtySyncFields != null && index >= 0) {
                    dirtySyncFields.set(index, changed);
                    owner.onSyncChanged(ref, changed);
                }
            }, (ref, index, changed) -> {
                if (dirtyPersistedFields != null && index >= 0) {
                    dirtyPersistedFields.set(index, changed);
                    owner.onPersistedChanged(ref, changed);
                }
            });

            syncFields = result.syncedRefs();
            persistedFields = result.persistedRefs();

            dirtySyncFields = new BitSet(syncFields.length);
            dirtyPersistedFields = new BitSet(result.persistedRefs().length);

            nonLazyFields = result.nonLazyFields();
            fieldMap = result.fieldRefMap();
            initialized = true;
        } finally {
            lock.unlock();
        }
    }

    public FieldManagedStorage(IManaged owner) {
        this.owner = owner;
    }

    public IRef[] getSyncFields() {
        init();
        return syncFields;
    }

    @Override
    public boolean hasDirtySyncFields() {
        return !dirtySyncFields.isEmpty();
    }

    @Override
    public boolean hasDirtyPersistedFields() {
        return !dirtyPersistedFields.isEmpty();
    }

    public IRef[] getPersistedFields() {
        init();
        return persistedFields;
    }

    @Override
    public IManaged[] getManaged() {
        return new IManaged[]{owner};
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
