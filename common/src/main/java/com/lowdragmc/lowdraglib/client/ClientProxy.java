package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

@Environment(EnvType.CLIENT)
public class ClientProxy {

    /**
     * should be called when Minecraft is prepared.
     */
    public static void init() {
        Shaders.init();
        DrawerHelper.init();
        CompassManager.INSTANCE.init();
    }

    @ExpectPlatform
    public static ParticleProvider getProvider(ParticleType<?> type) {
        throw new AssertionError();
    }

}
