package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;

@NoArgsConstructor
public class SPacketUIWidgetUpdate implements IPacket {

    public int windowId;
    public FriendlyByteBuf updateData;

    public SPacketUIWidgetUpdate(int windowId, FriendlyByteBuf updateData) {
        this.windowId = windowId;
        this.updateData = updateData;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(updateData.readableBytes());
        buf.writeBytes(updateData);

        buf.writeVarInt(windowId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        this.updateData = new FriendlyByteBuf(copiedDataBuffer);

        this.windowId = buf.readVarInt();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute(IHandlerContext handler) {
        if (handler.isClient()) {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof ModularUIGuiContainer) {
                ((ModularUIGuiContainer) currentScreen).handleWidgetUpdate(this);
            }
        }
    }

}
