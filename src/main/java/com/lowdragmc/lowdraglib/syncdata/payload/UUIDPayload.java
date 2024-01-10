package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;

import java.util.UUID;

public class UUIDPayload extends ObjectTypedPayload<UUID> {
    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeUUID(payload);
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = buf.readUUID();
    }

    @Override
    public @Nullable Tag serializeNBT() {
        return NbtUtils.createUUID(payload);
    }

    @Override
    public void deserializeNBT(Tag tag) {
        payload = NbtUtils.loadUUID(tag);
    }
}
