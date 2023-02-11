package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientProxy {

    /**
     * should be called when Minecraft is prepared.
     */
    public static void init() {
        Shaders.init();
        DrawerHelper.init();
    }

}
