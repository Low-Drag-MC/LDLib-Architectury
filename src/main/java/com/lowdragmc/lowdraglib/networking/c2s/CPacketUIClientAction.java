package com.lowdragmc.lowdraglib.networking.c2s;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

@NoArgsConstructor
public class CPacketUIClientAction implements CustomPacketPayload {
    public static final ResourceLocation ID = LDLib.location("ui_client_action");
    public int windowId;
    public FriendlyByteBuf updateData;

    public CPacketUIClientAction(int windowId, FriendlyByteBuf updateData) {
        this.windowId = windowId;
        this.updateData = updateData;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(updateData.readableBytes());
        buf.writeBytes(updateData);

        buf.writeVarInt(windowId);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static CPacketUIClientAction decode(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        FriendlyByteBuf updateData = new FriendlyByteBuf(copiedDataBuffer);
        
        int windowId = buf.readVarInt();
        return new CPacketUIClientAction(windowId, updateData);
    }

    public static void execute(CPacketUIClientAction packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            AbstractContainerMenu openContainer = context.player().get().containerMenu;
            if (openContainer instanceof ModularUIContainer) {
                ((ModularUIContainer)openContainer).handleClientAction(packet);
            }
        });
    }
}
