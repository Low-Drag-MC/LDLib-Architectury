package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientProxy {
    public static void init() {
        Shaders.init();
        DrawerHelper.init();
    }

    public ClientProxy() {
        super();
        if (LDLib.isJeiLoaded()) {
            MinecraftForge.EVENT_BUS.register(JEIClientEventHandler.class);
        }
    }

}
