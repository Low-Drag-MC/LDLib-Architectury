package com.lowdragmc.lowdraglib.syncdata.payload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

public class FriendlyBufPayload extends ObjectTypedPayload<FriendlyByteBuf> {

    public static ITypedPayload<?> of(FriendlyByteBuf buf) {
        var payload = new FriendlyBufPayload();
        payload.setPayload(buf);
        return payload;
    }

    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeVarInt(buf.readableBytes());
        buf.writeBytes(buf);
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        this.payload = new FriendlyByteBuf(copiedDataBuffer);
    }

    @Override
    public Tag serializeNBT() {
        return new ByteArrayTag(Arrays.copyOfRange(payload.array(), 0, payload.writerIndex()));
    }

    @Override
    public void deserializeNBT(Tag buf) {
        if (buf instanceof ByteArrayTag byteTags) {
            payload = new FriendlyByteBuf(Unpooled.copiedBuffer(byteTags.getAsByteArray()));
        }
    }
}
