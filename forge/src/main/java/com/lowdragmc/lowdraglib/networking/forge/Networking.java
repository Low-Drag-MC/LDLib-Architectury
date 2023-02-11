package com.lowdragmc.lowdraglib.networking.forge;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.INetworking;
import com.lowdragmc.lowdraglib.networking.IPacket;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class Networking implements INetworking {
    @Getter
    protected final ResourceLocation networkingName;
    @Getter
    protected final String version;
    protected final SimpleChannel network;
    protected int AUTO_ID = 0;
    
    public Networking(ResourceLocation location, String version) {
        this.networkingName = location;
        this.version = version;
        network = NetworkRegistry.newSimpleChannel(location,
                () -> version,
                version::equals,
                version::equals);
    }

    public <MSG extends IPacket> void register(Class<MSG> clazz, NetworkDirection direction) {
        network.registerMessage(AUTO_ID++, clazz, IPacket::encode, buffer -> {
            try {
                MSG packet = clazz.newInstance();
                packet.decode(buffer);
                return packet;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }, (msg, ctx) -> {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> msg.execute(new IHandlerContext() {
                @Override
                public Object getContext() {
                    return context;
                }

                @Override
                public boolean isClient() {
                    return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
                }

                @Override
                public Player getPlayer() {
                    if (isClient()) {
                        return Minecraft.getInstance().player;
                    } else {
                        return context.getSender();
                    }
                }

                @Override
                public MinecraftServer getServer() {
                    return context.getSender() == null ? null : context.getSender().getServer();
                }

                @Override
                public Level getLevel() {
                    if (isClient()) {
                        return LogicalSidedProvider.CLIENTWORLD.get(context.getDirection().getReceptionSide()).orElse(null);
                    } else {
                        return getPlayer() instanceof ServerPlayer serverPlayer ? serverPlayer.getLevel() : null;
                    }
                }
            }));
            context.setPacketHandled(true);
        }, Optional.ofNullable(direction));
    }

    public <MSG extends IPacket> void registerC2S(Class<MSG> clazz) {
        this.register(clazz, NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG extends IPacket> void registerS2C(Class<MSG> clazz) {
        this.register(clazz, NetworkDirection.PLAY_TO_CLIENT);
    }

    public <MSG extends IPacket> void registerBoth(Class<MSG> clazz) {
        registerS2C(clazz);
        registerC2S(clazz);
    }

    public <T> void send(PacketDistributor.PacketTarget target, T packet) {
        network.send(target, packet);
    }

    public void sendToServer(IPacket msg) {
        network.sendToServer(msg);
    }

    public void sendToPlayer(IPacket msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            network.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void sendToTrackingChunk(IPacket packet, LevelChunk levelChunk) {
        send(PacketDistributor.TRACKING_CHUNK.with(() -> levelChunk), packet);

    }

    public void sendToAll(IPacket msg) {
        send(PacketDistributor.ALL.noArg(), msg);
    }

}
