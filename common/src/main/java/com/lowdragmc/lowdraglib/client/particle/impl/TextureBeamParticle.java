package com.lowdragmc.lowdraglib.client.particle.impl;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.particle.BeamParticle;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/06/15
 * @implNote TextureBeamParticle, texture beam particle
 */
@Environment(EnvType.CLIENT)
public class TextureBeamParticle extends BeamParticle {

    public ResourceLocation texture = new ResourceLocation(LDLib.MOD_ID, "textures/particle/laser.png");

    public TextureBeamParticle(ClientLevel level, Vector3 from, Vector3 end) {
        super(level, from, end);
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    protected static final Function<ResourceLocation, ParticleRenderType> TYPE = Util.memoize((texture) -> new ParticleRenderType() {
        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder, @Nonnull
        TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
        }
    });

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return TYPE.apply(texture);
    }
}
