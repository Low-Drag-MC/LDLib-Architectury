package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.accessor.IArrayLikeAccessor;
import com.lowdragmc.lowdraglib.syncdata.managed.*;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ManagedKey {
    private final String name;
    private final boolean isDestSync;
    private final boolean isGuiSync;
    private final boolean isPersist;
    private final boolean isDrop;

    @Nullable
    private String persistentKey;

    private final boolean isLazy;
    private final Type contentType;
    private final Field rawField;

    public String getName() {
        return name;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public boolean isDestSync() {
        return isDestSync;
    }

    public boolean isGuiSync() {
        return isGuiSync;
    }

    public boolean isPersist() {
        return isPersist;
    }

    public boolean isDrop() {
        return isDrop;
    }


    public @Nullable String getPersistentKey() {
        return persistentKey;
    }

    public void setPersistentKey(@Nullable String persistentKey) {
        this.persistentKey = persistentKey;
    }

    public Type getContentType() {
        return contentType;
    }

    public Field getRawField() {
        return rawField;
    }

    public ManagedKey(String name, boolean isDestSync, boolean isGuiSync, boolean isPersist, boolean isDrop, boolean isLazy, Type contentType, Field rawField) {
        this.name = name;
        this.isDestSync = isDestSync;
        this.isGuiSync = isGuiSync;
        this.isPersist = isPersist;
        this.isDrop = isDrop;
        this.isLazy = isLazy;
        this.contentType = contentType;
        this.rawField = rawField;
    }

    private IAccessor accessor;

    public IAccessor getAccessor() {
        if (accessor == null) {
            accessor = TypedPayloadRegistries.findByType(contentType);
        }
        return accessor;
    }

    public ITypedPayload<?> readSyncedField(IRef field) {
        return getAccessor().readField(field);
    }

    public void writeSyncedField(IRef field, ITypedPayload<?> payload) {
        getAccessor().writeField(field, payload);
    }

    public Tag readPersistedField(IRef field) {
        return getAccessor().readField(field).serializeNBT();
    }

    public void writePersistedField(IRef field, @NotNull Tag nbt) {
        var payloadType = getAccessor().getDefaultType();
        var payload = TypedPayloadRegistries.create(payloadType);
        payload.deserializeNBT(nbt);
        getAccessor().writeField(field, payload);
    }

    public IRef createRef(Object instance) {
        try {

            var accessor = getAccessor();

            if(accessor instanceof IArrayLikeAccessor arrayLikeAccessor) {

                if(accessor.isManaged() || arrayLikeAccessor.getChildAccessor().isManaged()) {
                    return new ManagedArrayLikeRef(ManagedField.of(rawField, instance), isLazy).setKey(this);
                }
                try {

                    rawField.setAccessible(true);
                    return new ReadonlyArrayRef(isLazy, rawField.get(instance)).setKey(this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (accessor.isManaged()) {
                return ManagedRef.create(ManagedField.of(rawField, instance), isLazy).setKey(this);
            }
            try {
                rawField.setAccessible(true);
                return new ReadonlyRef(isLazy, rawField.get(instance)).setKey(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }catch (Exception e) {
            throw new IllegalStateException("Failed to create ref of " + this.name + " with type:" + this.rawField.getType().getCanonicalName(), e);
        }
    }
}
