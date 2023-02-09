package com.lowdragmc.lowdraglib.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ClientPlatform
 */
@Environment(EnvType.CLIENT)
public class ClientPlatform {
    @ExpectPlatform
    public static RenderType getRenderType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setRenderType(RenderType oldRenderLayer) {
        throw new AssertionError();
    }
}
