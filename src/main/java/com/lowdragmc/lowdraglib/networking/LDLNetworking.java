package com.lowdragmc.lowdraglib.networking;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.networking.both.PacketRPCMethodPayload;
import com.lowdragmc.lowdraglib.networking.c2s.CPacketUIClientAction;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIOpen;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

/**
 * Author: KilaBash
 * Date: 2022/04/27
 * Description:
 */
public class LDLNetworking {

    public static void registerPayloads(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registar = event.registrar(LDLib.MOD_ID);

        registar.play(SPacketUIOpen.ID, SPacketUIOpen::decode, builder -> builder.client(SPacketUIOpen::execute));
        registar.play(SPacketUIWidgetUpdate.ID, SPacketUIWidgetUpdate::decode, builder -> builder.client(SPacketUIWidgetUpdate::execute));
        registar.play(SPacketManagedPayload.ID, SPacketManagedPayload::decode, builder -> builder.client(SPacketManagedPayload::execute));

        registar.play(CPacketUIClientAction.ID, CPacketUIClientAction::decode, builder -> builder.server(CPacketUIClientAction::execute));

        registar.play(PacketRPCMethodPayload.ID, PacketRPCMethodPayload::decode, builder -> builder
                .client(PacketRPCMethodPayload::executeClient)
                .server(PacketRPCMethodPayload::executeServer));
    }

    public static void sendToTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload packet) {
        level.getChunkSource().chunkMap.getPlayers(pos, false).forEach(p -> p.connection.send(packet));

    }

}
