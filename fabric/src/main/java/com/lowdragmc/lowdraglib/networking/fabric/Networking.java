package com.lowdragmc.lowdraglib.networking.fabric;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.fabric.PlatformImpl;
import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.INetworking;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import javax.annotation.Nullable;

public class Networking implements INetworking {
    @Getter
    protected final ResourceLocation networkingName;
    @Getter
    protected final String version;
    protected final BiMap<Class<? extends IPacket>, Integer> packetRegistry = HashBiMap.create();
    protected int AUTO_ID = 0;
    
    public Networking(ResourceLocation location, String version) {
        this.networkingName = location;
        this.version = version;
    }

    public int getPacketId(Class<? extends IPacket> clazz) {
        return packetRegistry.get(clazz);
    }

    public ResourceLocation getChannel(Class<? extends IPacket> clazz) {
        return new ResourceLocation(networkingName.getNamespace(), networkingName.getPath() + "-" + getPacketId(clazz));
    }

    public <MSG extends IPacket> void register(Class<MSG> clazz, boolean isC2S) {
        packetRegistry.put(clazz, AUTO_ID++);
        if (isC2S) {
            ServerPlayNetworking.registerGlobalReceiver(getChannel(clazz), (server, player, handler, buf, responseSender) -> {
                try {
                    MSG packet = clazz.newInstance();
                    packet.decode(buf);
                    server.execute(() -> packet.execute(new IHandlerContext() {
                        @Override
                        public Object getContext() {
                            return handler;
                        }

                        @Override
                        public boolean isClient() {
                            return false;
                        }

                        @Override
                        public @Nullable Player getPlayer() {
                            return player;
                        }

                        @Override
                        public MinecraftServer getServer() {
                            return server;
                        }

                        @Override
                        public Level getLevel() {
                            return player.getLevel();
                        }
                    }));
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } else if (LDLib.isClient()){
            ClientPlayNetworking.registerGlobalReceiver(getChannel(clazz), (client, handler, buf, responseSender) -> {
                try {
                    MSG packet = clazz.newInstance();
                    packet.decode(buf);
                    client.execute(() -> packet.execute(new IHandlerContext() {
                        @Override
                        public Object getContext() {
                            return handler;
                        }

                        @Override
                        public boolean isClient() {
                            return true;
                        }

                        @Override
                        public @Nullable Player getPlayer() {
                            return client.player;
                        }

                        @Override
                        public @Nullable MinecraftServer getServer() {
                            return null;
                        }

                        @Override
                        public Level getLevel() {
                            return handler.getLevel();
                        }
                    }));
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public <MSG extends IPacket> void registerC2S(Class<MSG> clazz) {
        this.register(clazz, true);
    }

    public <MSG extends IPacket> void registerS2C(Class<MSG> clazz) {
        this.register(clazz, false);
    }

    public <MSG extends IPacket> void registerBoth(Class<MSG> clazz) {
        registerS2C(clazz);
        registerC2S(clazz);
    }

    public void sendToServer(IPacket msg) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        msg.encode(buffer);
        ClientPlayNetworking.send(getChannel(msg.getClass()), buffer);
    }

    public void sendToPlayer(IPacket msg, ServerPlayer player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        msg.encode(buffer);
        ServerPlayNetworking.send(player, getChannel(msg.getClass()), buffer);
    }

    @Override
    public void sendToTrackingChunk(IPacket msg, LevelChunk levelChunk) {
        if (levelChunk.getLevel().getChunkSource() instanceof ServerChunkCache chunkCache) {
            chunkCache.chunkMap.getPlayers(levelChunk.getPos(), false).forEach(player -> sendToPlayer(msg, player));
        }
    }

    public void sendToAll(IPacket msg) {
        for (ServerPlayer serverPlayer : PlatformImpl.getMinecraftServer().getPlayerList().getPlayers()) {
            sendToPlayer(msg, serverPlayer);
        }
    }

}
