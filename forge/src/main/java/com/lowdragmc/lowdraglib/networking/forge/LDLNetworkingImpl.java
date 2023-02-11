package com.lowdragmc.lowdraglib.networking.forge;

import com.lowdragmc.lowdraglib.networking.INetworking;
import net.minecraft.resources.ResourceLocation;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote LDLNetworkingImpl
 */
public class LDLNetworkingImpl {

    public static INetworking createNetworking(ResourceLocation networking, String version) {
        return new Networking(networking, version);
    }

}
