package com.lowdragmc.lowdraglib.fabric.core.mixins.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author KilaBash
 * @date 202311/9
 * @implNote ParticleEngineAccessor
 */
@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor
    Int2ObjectMap<ParticleProvider<?>> getProviders();
}
