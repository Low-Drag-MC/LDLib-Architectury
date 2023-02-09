package com.lowdragmc.lowdraglib.syncdata;

import net.minecraft.network.FriendlyByteBuf;

public interface IBytebufSerializable {
    void toBytes(FriendlyByteBuf buf);
    void fromBytes(FriendlyByteBuf buf);
}
