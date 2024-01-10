package com.lowdragmc.lowdraglib.syncdata.accessor;


import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;

import java.util.Objects;

public abstract class CustomObjectAccessor<T> extends ManagedAccessor {

    private final Class<T> type;
    private final boolean includesChildren;

    private final Class<?>[] operandTypes;

    protected CustomObjectAccessor(Class<T> type, boolean includesChildren) {
        this.type = type;
        this.includesChildren = includesChildren;
        this.operandTypes = new Class<?>[] { type };
    }

    @Override
    public boolean hasPredicate() {
        return includesChildren;
    }

    @Override
    public boolean test(Class<?> type) {
        return this.type.isAssignableFrom(type);
    }

    @Override
    public Class<?>[] operandTypes() {
        return operandTypes;
    }

    public abstract ITypedPayload<?> serialize(AccessorOp op, T value);
    public abstract T deserialize(AccessorOp op, ITypedPayload<?> payload);

    @Override
    public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
        var value = field.value();
        if (value != null) {
            if (!type.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("Value %s is not assignable to type %s".formatted(value, type));
            }
            //noinspection unchecked
            return serialize(op, (T) value);
        }
        return PrimitiveTypedPayload.ofNull();
    }

    @Override
    public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
        if (payload instanceof PrimitiveTypedPayload<?> primitive && primitive.isNull()) {
            field.set(null);
            return;
        }

        var result = deserialize(op, payload);
        Objects.requireNonNull(result, "Payload %s is not a valid payload for type %s".formatted(payload, type));
        ((IManagedVar<T>) field).set(result);

    }
}
