package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class StringPayload extends ObjectTypedPayload<String> {

    @Override
    public void writePayload(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(payload);
    }

    @Override
    public void readPayload(RegistryFriendlyByteBuf buf) {
        payload = buf.readUtf();
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        return StringTag.valueOf(payload);
    }

    @Override
    public void deserializeNBT(Tag tag, HolderLookup.Provider provider) {
        payload = tag.getAsString();
    }

    public static StringPayload of(String value) {
        var payload = new StringPayload();
        payload.setPayload(value);
        return payload;
    }
}
