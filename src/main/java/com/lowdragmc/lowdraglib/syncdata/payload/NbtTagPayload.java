package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.nbt.Tag;

public class NbtTagPayload extends ObjectTypedPayload<Tag> {

    public static ITypedPayload<?> of(Tag tag) {
        var payload = new NbtTagPayload();
        payload.setPayload(tag);
        return payload;
    }

    @Override
    public Tag serializeNBT() {
        return payload;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        payload = tag;
    }
}
