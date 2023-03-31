package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.LazyManaged;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * A block entity that can be automatically synced with the client.
 *
 * @see DescSynced
 * @see LazyManaged
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


    default void defaultServerTick() {
        for (IRef field : getNonLazyFields()) {
            field.update();
        }
        if (getRootStorage().hasDirtyFields()) {
            var packet = SPacketManagedPayload.of(this, false);
            LDLNetworking.NETWORK.sendToTrackingChunk(packet, Objects.requireNonNull(this.getSelf().getLevel()).getChunkAt(this.getCurrentPos()));
        }
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


    /**
     * sync tag name
     */
    default String getSyncTag() {
        return "sync";
    }
}
