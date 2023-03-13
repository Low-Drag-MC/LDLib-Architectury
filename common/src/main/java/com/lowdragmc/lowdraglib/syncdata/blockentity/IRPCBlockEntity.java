package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.both.PacketRPCMethodPayload;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.field.RPCMethodMeta;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import javax.annotation.Nullable;

public interface IRPCBlockEntity extends IManagedBlockEntity {

    /**
     * Get the RPC method
     */
    @Nullable
    default RPCMethodMeta getRPCMethod(IManaged managed, String methodName) {
        return managed.getFieldHolder().getRpcMethodMap().get(methodName);
    }

    default PacketRPCMethodPayload generateRpcPacket(IManaged managed, String methodName, Object... args) {
        return PacketRPCMethodPayload.of(managed, this, methodName,args);
    }

    @Environment(EnvType.CLIENT)
    default void rpcToServer(IManaged managed, String methodName, Object... args) {
        var packet = generateRpcPacket(managed, methodName, args);
        LDLNetworking.NETWORK.sendToServer(packet);
    }


    default void rpcToPlayer(IManaged managed, ServerPlayer player, String methodName, Object... args) {
        var packet = generateRpcPacket(managed, methodName, args);
        LDLNetworking.NETWORK.sendToPlayer(packet, player);
    }

    default void rpcToTracking(IManaged managed, ServerPlayer player, String methodName, Object... args) {
        var packet = generateRpcPacket(managed, methodName, args);
        LDLNetworking.NETWORK.sendToTrackingChunk(packet, getSelf().getLevel().getChunkAt(getSelf().getBlockPos()));
    }

}
