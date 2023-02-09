package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;

import java.util.function.Predicate;

/**
 * Accessor is a class that can read and write a field of a specific type.
 */
public interface IAccessor extends Predicate<Class<?>> {
    ITypedPayload<?> readField(IRef field);

    void writeField(IRef field, ITypedPayload<?> payload);

    default boolean hasPredicate() {
        return false;
    }

    default boolean test(Class<?> type) {
        return false;
    }

    default Class<?>[] operandTypes() {
        return new Class<?>[0];
    }

    boolean isManaged();

    void setDefaultType(byte payloadType);
    byte getDefaultType();


    default ITypedPayload<?> readFromReadonlyField(Object obj) {
        throw new UnsupportedOperationException();
    }

    default void writeToReadonlyField(Object obj, ITypedPayload<?> payload) {
        throw new UnsupportedOperationException();
    }


    default ITypedPayload<?> readManagedField(IManagedVar<?> field) {
        throw new UnsupportedOperationException();
    }

    default void writeManagedField(IManagedVar<?> field, ITypedPayload<?> payload) {
        throw new UnsupportedOperationException();
    }
}
