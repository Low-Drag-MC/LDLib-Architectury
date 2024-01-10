package com.lowdragmc.lowdraglib.client.scene;

import com.lowdragmc.lowdraglib.LDLib;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3f;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: KilaBash
 * @Date: 2021/08/23
 * @Description: It looks similar to {@link ImmediateWorldSceneRenderer}, but totally different.
 * It uses FBO and is more universality and efficient(X).
 * FBO can be rendered anywhere more flexibly, not just in the GUI.
 * If you have scene rendering needs, you will love this FBO renderer.
 * TODO OP_LIST might be used in the future to further improve performance.
 */
@OnlyIn(Dist.CLIENT)
public class FBOWorldSceneRenderer extends WorldSceneRenderer {
    private int resolutionWidth = 1080;
    private int resolutionHeight = 1080;
    private RenderTarget fbo;

    public FBOWorldSceneRenderer(Level world, int resolutionWidth, int resolutionHeight) {
        super(world);
        setFBOSize(resolutionWidth, resolutionHeight);
    }

    public FBOWorldSceneRenderer(Level world, RenderTarget fbo) {
        super(world);
        this.fbo = fbo;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    /***
     * This will modify the size of the FBO. You'd better know what you're doing before you call it.
     */
    public void setFBOSize(int resolutionWidth, int resolutionHeight) {
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        releaseFBO();
        try {
            fbo = new MainTarget(resolutionWidth, resolutionHeight);
        } catch (Exception e) {
            LDLib.LOGGER.error("set FBO SIZE failed", e);
        }
    }

    public BlockHitResult screenPos2BlockPosFace(int mouseX, int mouseY) {
        int lastID = bindFBO();
        BlockHitResult looking = super.screenPos2BlockPosFace(mouseX, mouseY, 0, 0, this.resolutionWidth, this.resolutionHeight);
        unbindFBO(lastID);
        return looking;
    }

    public Vector3f blockPos2ScreenPos(BlockPos pos, boolean depth){
        int lastID = bindFBO();
        Vector3f winPos = super.blockPos2ScreenPos(pos, depth, 0, 0, this.resolutionWidth, this.resolutionHeight);
        unbindFBO(lastID);
        return winPos;
    }

    public void render(@Nonnull PoseStack poseStack, float x, float y, float width, float height, float mouseX, float mouseY) {
        // bind to FBO
        int lastID = bindFBO();
        super.render(new PoseStack(), 0, 0, this.resolutionWidth, this.resolutionHeight, (int) (this.resolutionWidth * (mouseX - x) / width), (int) (this.resolutionHeight * (1 - (mouseY - y) / height)));
        // unbind FBO
        unbindFBO(lastID);

        // bind FBO as texture
//        RenderSystem.enableTexture();
//        RenderSystem.disableLighting();
//        lastID = GL11.glGetInteger(GL11.GL_TEXTURE_2D);
//        fbo.bindRead();
//        RenderSystem.setShaderColor(1,1,1,1);

        // render rect with FBO texture
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, fbo.getColorTextureId());
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        var pose = poseStack.last().pose();
        bufferbuilder.vertex(pose, x + width, y + height, 0).uv(1, 0).endVertex();
        bufferbuilder.vertex(pose, x + width, y, 0).uv(1, 1).endVertex();
        bufferbuilder.vertex(pose, x, y, 0).uv(0, 1).endVertex();
        bufferbuilder.vertex(pose, x, y + height, 0).uv(0, 0).endVertex();
        tessellator.end();

//        RenderSystem.bindTexture(lastID);
    }

    public void render(@Nonnull PoseStack poseStack, float x, float y, float width, float height, int mouseX, int mouseY) {
        render(poseStack, x, y, width, height, (float) mouseX, (float) mouseY);
    }

    private int bindFBO(){
        int lastID = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
        fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
//        RenderSystem.pushMatrix();
        return lastID;
    }

    private void unbindFBO(int lastID){
//        RenderSystem.popMatrix();
        fbo.unbindRead();
        GlStateManager._glBindFramebuffer(36160, lastID);
    }

    public void releaseFBO() {
        if (fbo != null) {
            fbo.destroyBuffers();
        }
        fbo = null;
    }
}
