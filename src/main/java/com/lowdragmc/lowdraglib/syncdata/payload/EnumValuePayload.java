package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

import javax.annotation.Nullable;

public class EnumValuePayload extends ObjectTypedPayload<EnumValuePayload.Payload> {
    public EnumValuePayload() {
        setPayload(new Payload());
    }

    public static EnumValuePayload of(String name, int ordinal) {
        EnumValuePayload payload = new EnumValuePayload();
        payload.getPayload().name = name;
        payload.getPayload().ordinal = ordinal;
        return payload;
    }

    public static class Payload {
        public int ordinal = -1;
        public String name;
    }

    @Override
    public void writePayload(RegistryFriendlyByteBuf buf) {
        if (payload.ordinal == -1) {
            throw new IllegalStateException("Did not find ordinal for enum");
        }
        buf.writeVarInt(payload.ordinal);
    }

    @Override
    public void readPayload(RegistryFriendlyByteBuf buf) {
        payload.ordinal = buf.readVarInt();
    }

    @Override
    public @Nullable Tag serializeNBT(HolderLookup.Provider provider) {
        if (payload.name == null) {
            throw new IllegalStateException("Did not find name for enum");
        }
        return StringTag.valueOf(payload.name);
    }

    @Override
    public void deserializeNBT(Tag tag, HolderLookup.Provider provider) {
        // supports old formats and transform it
        if (tag instanceof IntTag) {
            payload.ordinal = ((IntTag) tag).getAsInt();
            return;
        }
        payload.name = tag.getAsString();
    }
}
