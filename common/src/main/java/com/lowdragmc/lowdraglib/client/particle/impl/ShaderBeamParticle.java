package com.lowdragmc.lowdraglib.client.particle.impl;

import com.lowdragmc.lowdraglib.client.particle.BeamParticle;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderProgram;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/06/15
 * @implNote ShaderBeamParticle
 */
@Environment(EnvType.CLIENT)
public class ShaderBeamParticle extends BeamParticle {
    public final ShaderBeamRenderType renderType;

    protected static final Function<ResourceLocation, ShaderBeamRenderType> TYPE = Util.memoize((texture) -> new ShaderBeamRenderType(texture));

    public ShaderBeamParticle(ClientLevel level, Vector3 from, Vector3 end, ShaderBeamRenderType renderType) {
        super(level, from, end);
        this.renderType = renderType;
    }

    public ShaderBeamParticle(ClientLevel level, Vector3 from, Vector3 end, ResourceLocation resourceLocation) {
        this(level, from, end, TYPE.apply(resourceLocation));
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    public static class ShaderBeamRenderType implements ParticleRenderType {
        ResourceLocation shader;
        Consumer<ShaderProgram> shaderProgramConsumer;

        public ShaderBeamRenderType(ResourceLocation shader) {
            this.shader = shader;
        }

        public ShaderBeamRenderType(ResourceLocation shader, Consumer<ShaderProgram> shaderProgramConsumer) {
            this(shader);
            this.shaderProgramConsumer = shaderProgramConsumer;
        }

        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder, @Nonnull TextureManager textureManager) {
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            int lastID = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            ShaderManager.getTempTarget().clear(false);
            RenderTarget target = ShaderManager.getInstance().renderFullImageInFramebuffer(ShaderManager.getTempTarget(), Shaders.load(Shader.ShaderType.FRAGMENT, shader), null, shaderProgramConsumer);

            GlStateManager._glBindFramebuffer(36160, lastID);
            if (!ShaderManager.getInstance().hasViewPort()) {
                GlStateManager._viewport(0, 0, mainTarget.viewWidth, mainTarget.viewHeight);
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, target.getColorTextureId());
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
        }
    }

}
