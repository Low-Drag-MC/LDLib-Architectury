package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import com.lowdragmc.lowdraglib.async.IAsyncLogic;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote IAsyncAutoSyncBlockEntity
 */
public interface IAsyncAutoSyncBlockEntity extends IAutoSyncBlockEntity, IAsyncLogic {

    default boolean useAsyncThread() {
        return true;
    }

    default void onValid() {
        if (useAsyncThread() && getSelf().getLevel() instanceof ServerLevel serverLevel) {
            AsyncThreadData.getOrCreate(serverLevel).addAsyncLogic(this);
        }
    }

    default void onInValid() {
        if (getSelf().getLevel() instanceof ServerLevel serverLevel) {
            AsyncThreadData.getOrCreate(serverLevel).removeAsyncLogic(this);
        }
    }

    @Override
    default void asyncTick(long periodID) {
        if (useAsyncThread() && !getSelf().isRemoved()) {
            for (IRef field : getNonLazyFields()) {
                field.update();
            }
            var dirtyFields = getSyncStorage().getDirtyFields();
            if (dirtyFields != null && !dirtyFields.isEmpty()) {
                Platform.getMinecraftServer().execute(() -> {
                    var packet = SPacketManagedPayload.of(this, false);
                    LDLNetworking.NETWORK.sendToTrackingChunk(packet, Objects.requireNonNull(this.getSelf().getLevel()).getChunkAt(this.getCurrentPos()));
                });
            }
        }
    }
}
