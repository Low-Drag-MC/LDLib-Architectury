package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

@NoArgsConstructor
public class SPacketUIWidgetUpdate implements CustomPacketPayload {
    public static final ResourceLocation ID = LDLib.location("ui_widget_update");
    public int windowId;
    public FriendlyByteBuf updateData;

    public SPacketUIWidgetUpdate(int windowId, FriendlyByteBuf updateData) {
        this.windowId = windowId;
        this.updateData = updateData;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(updateData.readableBytes());
        buf.writeBytes(updateData);

        buf.writeVarInt(windowId);
    }

    public static SPacketUIWidgetUpdate decode(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        FriendlyByteBuf updateData = new FriendlyByteBuf(copiedDataBuffer);

        int windowId = buf.readVarInt();
        return new SPacketUIWidgetUpdate(windowId, updateData);
    }

    @OnlyIn(Dist.CLIENT)
    public static void execute(SPacketUIWidgetUpdate packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {

        });
        context.workHandler().submitAsync(() -> {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof ModularUIGuiContainer) {
                ((ModularUIGuiContainer) currentScreen).handleWidgetUpdate(packet);
            }
        });
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
