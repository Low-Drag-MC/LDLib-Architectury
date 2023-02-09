package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;

public abstract class ReadonlyAccessor implements IAccessor {

    private byte defaultType = -1;

    @Override
    public byte getDefaultType() {
        return defaultType;
    }

    @Override
    public void setDefaultType(byte defaultType) {
        this.defaultType = defaultType;
    }

    public abstract ITypedPayload<?> readFromReadonlyField(Object obj);

    public abstract void writeToReadonlyField(Object obj, ITypedPayload<?> payload);

    @Override
    public boolean isManaged() {
        return false;
    }

    @Override
    public ITypedPayload<?> readField(IRef field) {
        var obj = field.readRaw();
        if (obj == null) {
            throw new IllegalArgumentException("readonly field %s has a null reference".formatted(field.getKey()));
        }

        return readFromReadonlyField(obj);
    }

    @Override
    public void writeField(IRef field, ITypedPayload<?> payload) {
        var obj = field.readRaw();
        if (obj == null) {
            throw new IllegalArgumentException("readonly field %s has a null reference".formatted(field));
        }

        writeToReadonlyField(obj, payload);
    }

}
