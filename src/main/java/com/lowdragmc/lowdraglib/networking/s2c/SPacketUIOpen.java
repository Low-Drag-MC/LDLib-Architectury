package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

@NoArgsConstructor
public class SPacketUIOpen implements CustomPacketPayload {
    public static ResourceLocation ID = LDLib.location("ui_open");
    private ResourceLocation uiFactoryId;
    private FriendlyByteBuf serializedHolder;
    private int windowId;

    public SPacketUIOpen(ResourceLocation uiFactoryId, FriendlyByteBuf serializedHolder, int windowId) {
        this.uiFactoryId = uiFactoryId;
        this.serializedHolder = serializedHolder;
        this.windowId = windowId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(serializedHolder.readableBytes());
        buf.writeBytes(serializedHolder);

        buf.writeResourceLocation(uiFactoryId);
        buf.writeVarInt(windowId);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static SPacketUIOpen decode(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        FriendlyByteBuf serializedHolder = new FriendlyByteBuf(copiedDataBuffer);

        ResourceLocation uiFactoryId = buf.readResourceLocation();
        int windowId = buf.readVarInt();
        return new SPacketUIOpen(uiFactoryId, serializedHolder, windowId);
    }

    @OnlyIn(Dist.CLIENT)
    public static void execute(SPacketUIOpen packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            UIFactory<?> uiFactory = UIFactory.FACTORIES.get(packet.uiFactoryId);
            if (uiFactory != null) {
                uiFactory.initClientUI(packet.serializedHolder, packet.windowId);
            }
        });
    }

}
