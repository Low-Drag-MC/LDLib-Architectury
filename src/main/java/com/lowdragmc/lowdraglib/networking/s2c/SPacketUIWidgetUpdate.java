package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@NoArgsConstructor
public class SPacketUIWidgetUpdate implements CustomPacketPayload {
    public static final ResourceLocation ID = LDLib.location("ui_widget_update");
    public static final Type<SPacketUIWidgetUpdate> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketUIWidgetUpdate> CODEC = StreamCodec.ofMember(SPacketUIWidgetUpdate::write, SPacketUIWidgetUpdate::decode);

    public int windowId;
    public RegistryFriendlyByteBuf updateData;

    public SPacketUIWidgetUpdate(int windowId, RegistryFriendlyByteBuf updateData) {
        this.windowId = windowId;
        this.updateData = updateData;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(updateData.readableBytes());
        buf.writeBytes(updateData);

        buf.writeVarInt(windowId);

        updateData.resetReaderIndex();
    }

    public static SPacketUIWidgetUpdate decode(RegistryFriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        RegistryFriendlyByteBuf updateData = new RegistryFriendlyByteBuf(copiedDataBuffer, buf.registryAccess());

        int windowId = buf.readVarInt();
        return new SPacketUIWidgetUpdate(windowId, updateData);
    }

    @OnlyIn(Dist.CLIENT)
    public static void execute(SPacketUIWidgetUpdate packet, IPayloadContext context) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof ModularUIGuiContainer) {
            ((ModularUIGuiContainer) currentScreen).handleWidgetUpdate(packet);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
