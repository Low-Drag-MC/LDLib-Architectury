package com.lowdragmc.lowdraglib.networking;

import net.minecraft.network.FriendlyByteBuf;

public interface IPacket {

    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);

    default void execute(IHandlerContext handler) {
        
    }

}