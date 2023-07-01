package com.lowdragmc.lowdraglib.networking.c2s;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;

@NoArgsConstructor
public class CPacketUIClientAction implements IPacket {

    public int windowId;
    public FriendlyByteBuf updateData;

    public CPacketUIClientAction(int windowId, FriendlyByteBuf updateData) {
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
    public void execute(IHandlerContext handler) {
        if (handler.getPlayer() != null) {
            AbstractContainerMenu openContainer = handler.getPlayer().containerMenu;
            if (openContainer instanceof ModularUIContainer) {
                ((ModularUIContainer)openContainer).handleClientAction(this);
            }
        }
    }
}
