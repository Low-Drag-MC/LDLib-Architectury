package com.lowdragmc.lowdraglib.networking;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.networking.both.PacketRPCMethodPayload;
import com.lowdragmc.lowdraglib.networking.c2s.CPacketUIClientAction;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIOpen;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: KilaBash
 * Date: 2022/04/27
 * Description:
 */
public class LDLNetworking {

    public static final INetworking NETWORK = createNetworking(new ResourceLocation(LDLib.MOD_ID, "networking"), "0.0.1");

    @ExpectPlatform
    public static INetworking createNetworking(ResourceLocation networking, String version) {
        throw new AssertionError();
    }

    public static void init() {
        NETWORK.registerS2C(SPacketUIOpen.class);
        NETWORK.registerS2C(SPacketUIWidgetUpdate.class);
        NETWORK.registerS2C(SPacketManagedPayload.class);

        NETWORK.registerC2S(CPacketUIClientAction.class);

        NETWORK.registerBoth(PacketRPCMethodPayload.class);
    }

}
