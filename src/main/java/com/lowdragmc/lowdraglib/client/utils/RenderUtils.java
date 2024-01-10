package com.lowdragmc.lowdraglib.client.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Stack;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {

    private static final Stack<int[]> scissorFrameStack = new Stack<>();

    @Deprecated
    public static void useScissor(int x, int y, int width, int height, Runnable codeBlock) {
        pushScissorFrame(x, y, width, height);
        try {
            codeBlock.run();
        } finally {
            popScissorFrame();
        }
    }

    /**
     * use a scissor for the current screen. it maintains a stack for deeper scissors.
     * @param poseStack current stack state
     * @param x screen pos x
     * @param y screen pos y
     * @param width screen pos width
     * @param height screen pos height
     * @param codeBlock inner rendering logic
     */
    @Deprecated
    public static void useScissor(@Nonnull PoseStack poseStack, int x, int y, int width, int height, Runnable codeBlock) {
        var pose = poseStack.last().pose();
        Vector4f pos = pose.transform(new Vector4f(x, y, 0, 1.0F));
        Vector4f size = pose.transform(new Vector4f(x + width, y + height, 0, 1.0F));

        x = (int) pos.x();
        y = (int) pos.y();
        width = (int) (size.x() - x);
        height = (int) (size.y() - y);

        pushScissorFrame(x, y, width, height);
        try {
            codeBlock.run();
        } finally {
            popScissorFrame();
        }
    }

    private static int[] peekFirstScissorOrFullScreen() {
        int[] currentTopFrame = scissorFrameStack.isEmpty() ? null : scissorFrameStack.peek();
        if (currentTopFrame == null) {
            Window window = Minecraft.getInstance().getWindow();
            return new int[]{0, 0, window.getWidth(), window.getHeight()};
        }
        return currentTopFrame;
    }

    private static void pushScissorFrame(int x, int y, int width, int height) {
        int[] parentScissor = peekFirstScissorOrFullScreen();
        int parentX = parentScissor[0];
        int parentY = parentScissor[1];
        int parentWidth = parentScissor[2];
        int parentHeight = parentScissor[3];

        boolean pushedFrame = false;
        if (x <= parentX + parentWidth && y <= parentY + parentHeight) {
            int newX = Math.max(x, parentX);
            int newY = Math.max(y, parentY);
            int newWidth = width - (newX - x);
            int newHeight = height - (newY - y);
            if (newWidth > 0 && newHeight > 0) {
                int maxWidth = parentWidth - (x - parentX);
                int maxHeight = parentHeight - (y - parentY);
                newWidth = Math.min(maxWidth, newWidth);
                newHeight = Math.min(maxHeight, newHeight);
                applyScissor(newX, newY, newWidth, newHeight);
                //finally, push applied scissor on top of scissor stack
                if (scissorFrameStack.isEmpty()) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                }
                scissorFrameStack.push(new int[]{newX, newY, newWidth, newHeight});
                pushedFrame = true;
            }
        }
        if (!pushedFrame) {
            if (scissorFrameStack.isEmpty()) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }
            scissorFrameStack.push(new int[]{parentX, parentY, parentWidth, parentHeight});
        }
    }

    private static void popScissorFrame() {
        scissorFrameStack.pop();
        int[] parentScissor = peekFirstScissorOrFullScreen();
        int parentX = parentScissor[0];
        int parentY = parentScissor[1];
        int parentWidth = parentScissor[2];
        int parentHeight = parentScissor[3];
        applyScissor(parentX, parentY, parentWidth, parentHeight);
        if (scissorFrameStack.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    private static void applyScissor(int x, int y, int w, int h) {
        //translate upper-left to bottom-left
        Window window = Minecraft.getInstance().getWindow();
        double s = window.getGuiScale();
        int translatedY = window.getGuiScaledHeight() - y - h;
        GL11.glScissor((int)(x * s), (int)(translatedY * s), (int)(w * s), (int)(h * s));
    }

    /***
     * used to render pixels in stencil mask. (e.g. Restrict rendering results to be displayed only in Monitor Screens)
     * if you want to do the similar things in Gui(2D) not World(3D), plz consider using the {@link #useScissor(int, int, int, int, Runnable)}
     * that you don't need to draw mask to build a rect mask easily.
     * @param mask draw mask
     * @param renderInMask rendering in the mask
     * @param shouldRenderMask should mask be rendered too
     */
    public static void useStencil(Runnable mask, Runnable renderInMask, boolean shouldRenderMask) {
        GL11.glStencilMask(0xFF);
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        if (!shouldRenderMask) {
            GL11.glColorMask(false, false, false, false);
            GL11.glDepthMask(false);
        }

        mask.run();

        if (!shouldRenderMask) {
            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(true);
        }

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        renderInMask.run();

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static void renderBlockOverLay(@Nonnull PoseStack poseStack, BlockPos pos, float r, float g, float b, float scale) {
        if (pos == null) return;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        poseStack.pushPose();
        poseStack.translate((pos.getX() + 0.5), (pos.getY() + 0.5), (pos.getZ() + 0.5));
        poseStack.scale(scale, scale, scale);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtils.renderCubeFace(poseStack, buffer, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, r, g, b, 1);
        tessellator.end();

        poseStack.popPose();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void renderCubeFace(PoseStack poseStack, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        Matrix4f mat = poseStack.last().pose();
        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
    }

    public static void moveToFace(PoseStack poseStack, double x, double y, double z, Direction face) {
        poseStack.translate(x + 0.5 + face.getStepX() * 0.5, y + 0.5 + face.getStepY() * 0.5, z + 0.5 + face.getStepZ() * 0.5);
    }

    public static void rotateToFace(PoseStack poseStack, Direction face, @Nullable Direction spin) {
        float angle = spin == Direction.EAST ? Mth.HALF_PI : spin == Direction.SOUTH ? Mth.PI : spin == Direction.WEST ? -Mth.HALF_PI : 0;
        switch (face) {
            case UP -> {
                poseStack.scale(1.0f, -1.0f, 1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(Mth.HALF_PI, new Vector3f(1, 0, 0)));
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
            }
            case DOWN -> {
                poseStack.scale(1.0f, -1.0f, 1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(-Mth.HALF_PI, new Vector3f(1, 0, 0)));
                poseStack.mulPose(new Quaternionf().rotateAxis(spin == Direction.EAST ? Mth.HALF_PI : spin == Direction.NORTH ? Mth.PI : spin == Direction.WEST ? -Mth.HALF_PI : 0, new Vector3f(0, 0, 1)));
            }
            case EAST -> {
                poseStack.scale(-1.0f, -1.0f, -1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(-Mth.HALF_PI, new Vector3f(0, 1, 0)));
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
            }
            case WEST -> {
                poseStack.scale(-1.0f, -1.0f, -1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(Mth.HALF_PI, new Vector3f(0, 1, 0)));
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
            }
            case NORTH -> {
                poseStack.scale(-1.0f, -1.0f, -1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
            }
            case SOUTH -> {
                poseStack.scale(-1.0f, -1.0f, -1.0f);
                poseStack.mulPose(new Quaternionf().rotateAxis(Mth.PI, new Vector3f(0, 1, 0)));
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
            }
            default -> {
            }
        }
    }
}
