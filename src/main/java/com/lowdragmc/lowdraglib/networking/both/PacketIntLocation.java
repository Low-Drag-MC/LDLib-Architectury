package com.lowdragmc.lowdraglib.networking.both;

import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * a packet that contains a BlockPos
 */
@NoArgsConstructor
public abstract class PacketIntLocation implements CustomPacketPayload {
    protected BlockPos pos;

    public PacketIntLocation(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
