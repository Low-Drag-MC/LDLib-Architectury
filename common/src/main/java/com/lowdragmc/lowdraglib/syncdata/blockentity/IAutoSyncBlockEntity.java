package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.GuiSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.LazyManaged;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * A block entity that can be automatically synced with the client.
 * Note: you need to implement {@link #getFieldHolder()}, {@link #getSyncStorage()}, {@link #getSelf()}
 *
 * @see DescSynced
 * @see LazyManaged
 * @see GuiSynced
 */
public interface IAutoSyncBlockEntity extends IManagedBlockEntity {


    /**
     * do a sync now. if the block entity is tickable then this would be handled automatically, I think.
     *
     * @param force if true, all fields will be synced, otherwise only the ones that have changed will be synced
     */
    default void syncNow(boolean force) {
        var level = Objects.requireNonNull(getSelf().getLevel());
        if (level.isClientSide) {
            throw new IllegalStateException("Cannot sync from client");
        }
        for (IRef field : this.getNonLazyFields()) {
            field.update();
        }
        var packet = SPacketManagedPayload.of(this, force);
        LDLNetworking.NETWORK.sendToTrackingChunk(packet, level.getChunkAt(this.getCurrentPos()));
    }

    /**
     * Marks a field as changed, so it will be synced.
     *
     * @param name the key of the field, usually its name
     */
    default void markDirty(String name) {
        markDirty(getFieldHolder().getSyncedFieldIndex(name));
    }

    /**
     * Marks a field as changed, so it will be synced.
     *
     * @param index the index of the field
     */
    default void markDirty(int index) {
        getSyncedFieldRefs()[index].setChanged(true);
    }

    /**
     * Marks a field as changed, so it will be synced.
     *
     * @param key the key of the field
     */
    default void markDirty(ManagedKey key) {
        getSyncStorage().getFieldByKey(key).setChanged(true);
    }

    /**
     * Get all fields that is managed for auto sync.
     */
    default IRef[] getSyncedFieldRefs() {
        return getSyncStorage().getSyncFields();
    }


    default void defaultServerTick() {
        IManagedBlockEntity.super.defaultServerTick();
        var dirtyFields = getSyncStorage().getDirtyFields();
        if (dirtyFields != null && !dirtyFields.isEmpty()) {
            var packet = SPacketManagedPayload.of(this, false);
            LDLNetworking.NETWORK.sendToTrackingChunk(packet, Objects.requireNonNull(this.getSelf().getLevel()).getChunkAt(this.getCurrentPos()));
        }
    }

    /**
     * notify the client that the block entity has received new data form the server
     * so some rendering can be updated
     *
     * @param changedFields: The changed field names
     */
    @Environment(EnvType.CLIENT)
    default void onDescUpdate(ManagedKey[] changedFields) {

    }

    /**
     * add a listener to field update
     *
     * @param <T>      field type;
     * @param key      managed key
     * @param listener listener
     * @return callback that you can unsubscribe
     */
    @Environment(EnvType.CLIENT)
    default <T> Subscription addSyncUpdateListener(ManagedKey key, FieldUpdateListener<T> listener) {
        return getSyncStorage().addSyncUpdateListener(key, listener);
    }

    @Environment(EnvType.CLIENT)
    default void removeSyncUpdateListeners(ManagedKey key) {
        getSyncStorage().removeAllSyncUpdateListener(key);
    }

    /**
     * add a listener to field update by name
     */
    @Environment(EnvType.CLIENT)
    default <T> Subscription addSyncUpdateListener(String name, FieldUpdateListener<T> listener) {
        return addSyncUpdateListener(getFieldHolder().getSyncedFieldIndex(name), listener);
    }

    /**
     * write custom data to the packet
     */
    default void writeCustomSyncData(CompoundTag tag) {
    }

    /**
     * read custom data from the packet
     */
    default void readCustomSyncData(CompoundTag tag) {
    }


    @FunctionalInterface
    interface FieldUpdateListener<T> {
        void onFieldChanged(String changedField, T oldValue, T newValue);
    }

    interface Subscription {
        void unsubscribe();
    }
}
