package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketRPCMethodPayload;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.field.RPCMethodMeta;
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

    default SPacketRPCMethodPayload generateRpcPacket(IManaged managed, String methodName, Object... args) {
        return SPacketRPCMethodPayload.of(managed, this, methodName,args);
    }

    default void rpcToPlayer(IManaged managed, ServerPlayer player, String methodName, Object... args) {
        var packet = generateRpcPacket(managed, methodName, args);
        LDLNetworking.NETWORK.sendToPlayer(packet, player);
    }

    default void rpcToTracking(IManaged managed, String methodName, Object... args) {
        var packet = generateRpcPacket(managed, methodName, args);
        LDLNetworking.NETWORK.sendToTrackingChunk(packet, getSelf().getLevel().getChunkAt(getSelf().getBlockPos()));
    }

}
