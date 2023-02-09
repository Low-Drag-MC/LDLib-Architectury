package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.managed.*;
import com.lowdragmc.lowdraglib.syncdata.payload.ArrayPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;

import java.lang.reflect.Array;

public class ArrayAccessor implements IAccessor, IArrayLikeAccessor {

    @Override
    public byte getDefaultType() {
        return TypedPayloadRegistries.getId(ArrayPayload.class);
    }

    @Override
    public void setDefaultType(byte defaultType) {
        throw new UnsupportedOperationException("Cannot set default type for array accessor");
    }

    private final IAccessor childAccessor;
    private final Class<?> childType;

    public ArrayAccessor(IAccessor childAccessor, Class<?> childType) {
        this.childAccessor = childAccessor;
        this.childType = childType;
    }

    @Override
    public ITypedPayload<?> readField(IRef field) {

        if (field instanceof ManagedRef managedRef) {
            var managedField = managedRef.getField();
            if (!managedField.isPrimitive() && managedField.value() == null) {
                return PrimitiveTypedPayload.ofNull();
            }
            return readManagedField(managedField);
        }

        var obj = field.readRaw();
        if (obj == null) {
            throw new IllegalArgumentException("readonly field %s has a null reference".formatted(field));
        }

        return readFromReadonlyField(obj);
    }

    @Override
    public void writeField(IRef field, ITypedPayload<?> payload) {
        if (field instanceof ManagedRef syncedField) {
            var managedField = syncedField.getField();
            writeManagedField(managedField, payload);
        }
        var obj = field.readRaw();
        if (obj == null) {
            throw new IllegalArgumentException("readonly field %s has a null reference".formatted(field));
        }

        writeToReadonlyField(obj, payload);
    }

    @Override
    public boolean hasPredicate() {
        return true;
    }

    @Override
    public boolean test(Class<?> type) {
        return type.isArray();
    }

    @Override
    public boolean isManaged() {
        return childAccessor.isManaged();
    }

    @Override
    public ITypedPayload<?> readManagedField(IManagedVar<?> field) {
        var value = field.value();
        if (value == null) {
            return PrimitiveTypedPayload.ofNull();
        }

        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("Value %s is not an array".formatted(value));
        }

        var size = Array.getLength(value);
        var result = new ITypedPayload[size];
        if (!childAccessor.isManaged()) {
            for (int i = 0; i < size; i++) {
                var obj = Array.get(value, i);
                var payload = childAccessor.readFromReadonlyField(obj);
                result[i] = payload;
            }
        } else {
            for (int i = 0; i < size; i++) {
                var holder = ManagedArrayItem.of(value, i);
                var payload = childAccessor.readManagedField(holder);
                result[i] = payload;
            }

        }
        return ArrayPayload.of(result);
    }

    @Override
    public void writeManagedField(IManagedVar<?> field, ITypedPayload<?> payload) {
        if (payload instanceof PrimitiveTypedPayload<?> primitive && primitive.isNull()) {
            field.set(null);
            return;
        }
        if (!(payload instanceof ArrayPayload arrayPayload)) {
            throw new IllegalArgumentException("Payload %s is not ArrayPayload".formatted(payload));
        }
        boolean isManaged = childAccessor.isManaged();
        var result = field.value();
        if (result == null || Array.getLength(result) != arrayPayload.getPayload().length) {
            if (!isManaged) {
                throw new IllegalArgumentException("The array of %s should not be changed".formatted(childType));
            }
            result = Array.newInstance(childType, arrayPayload.getPayload().length);
        }
        var payloads = arrayPayload.getPayload();
        if (!isManaged) {
            for (int i = 0; i < payloads.length; i++) {
                var obj = Array.get(result, i);
                var item = payloads[i];
                childAccessor.writeToReadonlyField(obj, item);
            }
        } else {
            for (int i = 0; i < payloads.length; i++) {
                var holder = ManagedArrayItem.of(result, i);
                childAccessor.writeManagedField(holder, payloads[i]);
            }
        }

        if (isManaged) {
            //noinspection unchecked
            ((IManagedVar<Object>) field).set(result);
        }
    }

    @Override
    public ITypedPayload<?> readFromReadonlyField(Object obj) {
        var holder = ManagedHolder.of(obj);
        return readManagedField(holder);
    }

    @Override
    public void writeToReadonlyField(Object obj, ITypedPayload<?> payload) {
        var holder = ManagedHolder.of(obj);
        writeManagedField(holder, payload);
    }

    @Override
    public IAccessor getChildAccessor() {
        return childAccessor;
    }
}
