package com.lowdragmc.lowdraglib.client.scene;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.lowdragmc.lowdraglib.client.particle.LParticle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * @author KilaBash
 * @date 2022/06/05
 * @implNote ParticleManager, for LParticle
 */
@Environment(EnvType.CLIENT)
public class ParticleManager {
    private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);

    private final Map<ParticleRenderType, Queue<LParticle>> particles = Maps.newTreeMap(makeParticleRenderTypeComparator(RENDER_ORDER));

    private final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    public Level level;

    public void setLevel(Level level) {
        this.level = level;
    }

    public void addParticle(LParticle particle) {
        particle.setLevel(level);
        particles.computeIfAbsent(particle.getRenderType(), type -> Queues.newArrayDeque()).add(particle);
    }

    public void tick() {
        this.particles.forEach((particleRenderType, particleQueue) -> this.tickParticleList(particleQueue));
    }

    private void tickParticleList(Collection<LParticle> pParticles) {
        if (!pParticles.isEmpty()) {
            Iterator<LParticle> iterator = pParticles.iterator();
            while(iterator.hasNext()) {
                LParticle particle = iterator.next();
                particle.tick();
                if (!particle.isAlive()) {
                    iterator.remove();
                }
            }
        }

    }

    public void render(PoseStack pMatrixStack, Camera pActiveRenderInfo, float pPartialTicks) {
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(pMatrixStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        for(ParticleRenderType particlerendertype : this.particles.keySet()) {
            if (particlerendertype == ParticleRenderType.NO_RENDER) continue;
            Iterable<LParticle> iterable = this.particles.get(particlerendertype);
            if (iterable != null) {
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                particlerendertype.begin(bufferbuilder, this.textureManager);

                for(LParticle particle : iterable) {
                    try {
                        particle.render(bufferbuilder, pActiveRenderInfo, pPartialTicks);
                    } catch (Throwable throwable) {
                        throw throwable;
                    }
                }

                particlerendertype.end(tesselator);
            }
        }

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
    }

    public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder) {
        Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
        return (typeOne, typeTwo) ->
        {
            boolean vanillaOne = renderOrder.contains(typeOne);
            boolean vanillaTwo = renderOrder.contains(typeTwo);

            if (vanillaOne && vanillaTwo)
            {
                return vanillaComparator.compare(typeOne, typeTwo);
            }
            if (!vanillaOne && !vanillaTwo)
            {
                return Integer.compare(System.identityHashCode(typeOne), System.identityHashCode(typeTwo));
            }
            return vanillaOne ? -1 : 1;
        };
    }

}
