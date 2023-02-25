package com.lowdragmc.lowdraglib.syncdata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReadOnlyManaged {

    /**
     * specify a method e.g. {@code boolean methodName()}
     * return whether it has changed
     */
    String onDirtyMethod();

    /**
     * return a unique id (CompoundTag) of given instance.
     * e.g. {@code CompoundTag methodName(@Nonnull T obj)}
     * T - field type
     */
    String serializeMethod();

    /**
     * create an instance via given uid from server.
     * e.g. {@code T methodName(@Nonnull CompoundTag tag)}
     * T - field type
     */
    String deserializeMethod();
}
