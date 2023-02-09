package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.SyncUtils;

class SimpleObjectRef extends ManagedRef {
    private Object oldValue;

    SimpleObjectRef(IManagedVar<?> field) {
        super(field);
    }

    @Override
    public void update() {
        Object newValue = getField().value();
        if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null) || (oldValue != null && SyncUtils.isChanged(oldValue, newValue))) {
            oldValue = SyncUtils.copyWhenNecessary(newValue);
            setChanged(true);
        }
    }
}
