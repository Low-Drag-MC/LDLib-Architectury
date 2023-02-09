package com.lowdragmc.lowdraglib.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote INetworking
 */
public interface INetworking {

    ResourceLocation getNetworkingName();

    String getVersion();

    <MSG extends IPacket> void registerC2S(Class<MSG> clazz);

    <MSG extends IPacket> void registerS2C(Class<MSG> clazz);

    <MSG extends IPacket> void registerBoth(Class<MSG> clazz);

    void sendToServer(IPacket packet);

    void sendToAll(IPacket packet);

    void sendToPlayer(IPacket packet, ServerPlayer player);

    void sendToTrackingChunk(IPacket packet, LevelChunk levelChunk);

}
