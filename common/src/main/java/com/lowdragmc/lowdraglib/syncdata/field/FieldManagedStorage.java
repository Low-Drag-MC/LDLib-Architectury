package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.*;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class FieldManagedStorage implements IManagedStorage {

    private final IManaged owner;

    private BitSet dirtyFields;

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

    private void init() {
        lock.lock();
        try {
            if (initialized) {
                return;
            }
            ManagedKey[] fields = owner.getFieldHolder().getFields();

            var result = ManagedFieldUtils.getFieldRefs(fields, owner, (iS, iP, changed) -> {
                if (dirtyFields != null && iS >= 0) {
                    dirtyFields.set(iS, changed);
                }
                if (changed) {
                    if (iP >= 0) {
                        owner.onChanged();
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

    public FieldManagedStorage(IManaged owner) {
        this.owner = owner;
    }

    public IRef[] getSyncFields() {
        init();
        return syncFields;
    }

    @Override
    public boolean hasDirtyFields() {
        return !dirtyFields.isEmpty();
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
