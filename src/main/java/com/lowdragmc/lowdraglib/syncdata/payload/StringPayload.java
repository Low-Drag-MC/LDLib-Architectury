package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class StringPayload extends ObjectTypedPayload<String> {

    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeUtf(payload);
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = buf.readUtf();
    }

    @Override
    public Tag serializeNBT() {
        return StringTag.valueOf(payload);
    }

    @Override
    public void deserializeNBT(Tag tag) {
        payload = tag.getAsString();
    }

    public static StringPayload of(String value) {
        var payload = new StringPayload();
        payload.setPayload(value);
        return payload;
    }
}
