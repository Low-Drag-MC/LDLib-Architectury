package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class SPacketUIOpen implements CustomPacketPayload {
    public static final ResourceLocation ID = LDLib.location("ui_open");
    public static final Type<SPacketUIOpen> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketUIOpen> CODEC = StreamCodec.ofMember(SPacketUIOpen::write, SPacketUIOpen::decode);
    private ResourceLocation uiFactoryId;
    private RegistryFriendlyByteBuf serializedHolder;
    private int windowId;

    public SPacketUIOpen(ResourceLocation uiFactoryId, RegistryFriendlyByteBuf serializedHolder, int windowId) {
        this.uiFactoryId = uiFactoryId;
        this.serializedHolder = serializedHolder;
        this.windowId = windowId;
    }

    public void write(@NotNull RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(serializedHolder.readableBytes());
        buf.writeBytes(serializedHolder);

        buf.writeResourceLocation(uiFactoryId);
        buf.writeVarInt(windowId);

        serializedHolder.resetReaderIndex();
    }

    public static SPacketUIOpen decode(RegistryFriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        RegistryFriendlyByteBuf serializedHolder = new RegistryFriendlyByteBuf(copiedDataBuffer, buf.registryAccess());

        ResourceLocation uiFactoryId = buf.readResourceLocation();
        int windowId = buf.readVarInt();
        return new SPacketUIOpen(uiFactoryId, serializedHolder, windowId);
    }

    @OnlyIn(Dist.CLIENT)
    public static void execute(SPacketUIOpen packet, IPayloadContext context) {
        UIFactory<?> uiFactory = UIFactory.FACTORIES.get(packet.uiFactoryId);
        if (uiFactory != null) {
            uiFactory.initClientUI(packet.serializedHolder, packet.windowId);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
