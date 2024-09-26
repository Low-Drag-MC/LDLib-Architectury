package com.lowdragmc.lowdraglib.client.utils;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nonnull;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderBufferUtils {

    public static void drawLine(Matrix4f pose, VertexConsumer buffer, Vector3f from, Vector3f to,
                                float sr, float sg, float sb, float sa, float er, float eg, float eb, float ea) {
        var normal = new Vector3f(from).sub(to);
        buffer.vertex(pose, from.x, from.y, from.z).color(sr, sg, sb, sa)
                .normal(normal.x, normal.y, normal.z).endVertex();
        buffer.vertex(pose, to.x, to.y, to.z).color(er, eg, eb, ea).normal(normal.x, normal.y, normal.z)
                .endVertex();
    }

    public static void drawLines(PoseStack poseStack, VertexConsumer buffer, List<Vector3f> points, int colorStart, int colorEnd) {
        if (points.size() < 2) return;
        Matrix4f pose = poseStack.last().pose();
        Vector3f lastPoint = points.get(0);
        Vector3f point;
        int sa = (colorStart >> 24) & 0xff, sr = (colorStart >> 16) & 0xff, sg = (colorStart >> 8) & 0xff, sb = colorStart & 0xff;
        int ea = (colorEnd >> 24) & 0xff, er = (colorEnd >> 16) & 0xff, eg = (colorEnd >> 8) & 0xff, eb = colorEnd & 0xff;
        ea = (ea - sa);
        er = (er - sr);
        eg = (eg - sg);
        eb = (eb - sb);
        for (int i = 1; i < points.size(); i++) {
            float s = (i - 1f) / points.size();
            float e = i * 1f / points.size();
            point = points.get(i);
            drawLine(pose, buffer, lastPoint, point, (sr + er * s) / 255, (sg + eg * s) / 255, (sb + eb * s) / 255, (sa + ea * s) / 255,
                    (sr + er * e) / 255, (sg + eg * e) / 255, (sb + eb * e) / 255, (sa + ea * e) / 255);
        }
    }

    public static void drawCubeFrame(PoseStack poseStack, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        var mat = poseStack.last().pose();
        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).normal(1,0,0).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).normal(1,0,0).endVertex();

        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).normal(0,1,0).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).normal(0,1,0).endVertex();

        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).normal(0,0,1).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).normal(0,0,1).endVertex();

        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).normal(1,0,0).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).normal(1,0,0).endVertex();

        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).normal(0,1,0).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).normal(0,1,0).endVertex();

        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).normal(0,0,1).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).normal(0,0,1).endVertex();

        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).normal(0,0,1).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).normal(0,0,1).endVertex();

        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).normal(1,0,0).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).normal(1,0,0).endVertex();

        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).normal(0,0,1).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).normal(0,0,1).endVertex();

        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).normal(0,1,0).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).normal(0,1,0).endVertex();

        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).normal(1,0,0).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).normal(1,0,0).endVertex();

        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).normal(0,1,0).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).normal(0,1,0).endVertex();
    }

    public static void drawCubeFace(PoseStack poseStack, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float a, boolean shade) {
        Matrix4f mat = poseStack.last().pose();
        float r = red, g = green, b = blue;

        if (minZ != maxZ && minY != maxY) {
            if (shade) {
                r *= 0.6;
                g *= 0.6;
                b *= 0.6;
            }

            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
        }


        if (minX != maxX && minZ != maxZ ) {
            if (shade) {
                r = red * 0.5f;
                g = green * 0.5f;
                b = blue * 0.5f;
            }
            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();

            if (shade) {
                r = red;
                g = green;
                b = blue;
            }
            buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();

        }


        if (minX != maxX && minY != maxY) {
            if (shade) {
                r = red * 0.8f;
                g = green * 0.8f;
                b = blue * 0.8f;
            }
            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
        }
    }

    public static void renderCubeFace(PoseStack poseStack, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float a, boolean shade) {
        Matrix4f mat = poseStack.last().pose();
        float r = red, g = green, b = blue;

        if (shade) {
            r *= 0.6;
            g *= 0.6;
            b *= 0.6;
        }
        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();

        if (shade) {
            r = red * 0.5f;
            g = green * 0.5f;
            b = blue * 0.5f;
        }
        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();

        if (shade) {
            r = red;
            g = green;
            b = blue;
        }
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();

        if (shade) {
            r = red * 0.8f;
            g = green * 0.8f;
            b = blue * 0.8f;
        }
        buffer.vertex(mat, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(mat, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(r, g, b, a).endVertex();
    }

    public static void renderCubeFace(PoseStack poseStack, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color, int combinedLight, TextureAtlasSprite textureSprite) {
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        buffer.vertex(mat, minX, minY, minZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, -1, 0, 0).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, -1, 0, 0).endVertex();

        buffer.vertex(mat, maxX, minY, minZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 1, 0, 0).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 1, 0, 0).endVertex();


        buffer.vertex(mat, minX, minY, minZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, -1, 0).endVertex();
        buffer.vertex(mat, minX, minY, maxZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, -1, 0).endVertex();


        buffer.vertex(mat, minX, maxY, minZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 1, 0).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 1, 0).endVertex();

        buffer.vertex(mat, minX, minY, minZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, -1).endVertex();
        buffer.vertex(mat, minX, maxY, minZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, -1).endVertex();
        buffer.vertex(mat, maxX, maxY, minZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, -1).endVertex();
        buffer.vertex(mat, maxX, minY, minZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, -1).endVertex();

        buffer.vertex(mat, minX, minY, maxZ).color(color).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, 1).endVertex();
        buffer.vertex(mat, maxX, minY, maxZ).color(color).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, 1).endVertex();
        buffer.vertex(mat, maxX, maxY, maxZ).color(color).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, 1).endVertex();
        buffer.vertex(mat, minX, maxY, maxZ).color(color).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normal, 0, 0, 1).endVertex();
    }

    public static void drawColorLines(@Nonnull PoseStack poseStack, VertexConsumer builder, List<Vec2> points, int colorStart, int colorEnd, float width) {
        if (points.size() < 2) return;
        Matrix4f mat = poseStack.last().pose();
        Vec2 lastPoint = points.get(0);
        Vec2 point = points.get(1);
        Vector3f vec = null;
        int sa = (colorStart >> 24) & 0xff, sr = (colorStart >> 16) & 0xff, sg = (colorStart >> 8) & 0xff, sb = colorStart & 0xff;
        int ea = (colorEnd >> 24) & 0xff, er = (colorEnd >> 16) & 0xff, eg = (colorEnd >> 8) & 0xff, eb = colorEnd & 0xff;
        ea = (ea - sa);
        er = (er - sr);
        eg = (eg - sg);
        eb = (eb - sb);
        for (int i = 1; i < points.size(); i++) {
            float s = (i - 1f) / points.size();
            float e = i * 1f / points.size();
            point = points.get(i);
            vec = new Vector3f(point.x - lastPoint.x, point.y - lastPoint.y, 0).rotateZ(Mth.HALF_PI).normalize().mul(-width);
            builder.vertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0)
                    .color((sr + er * s) / 255, (sg + eg * s) / 255, (sb + eb * s) / 255, (sa + ea * s) / 255)
                    .endVertex();
            vec.mul(-1);
            builder.vertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0)
                    .color((sr + er * e) / 255, (sg + eg * e) / 255, (sb + eb * e) / 255, (sa + ea * e) / 255)
                    .endVertex();
            lastPoint = point;
        }
        vec.mul(-1);
        builder.vertex(mat, point.x + vec.x, point.y + vec.y, 0)
                .color(sr + er, sg + eg, sb + eb, sa + ea)
                .endVertex();
        vec.mul(-1);
        builder.vertex(mat, point.x + vec.x, point.y + vec.y, 0)
                .color(sr + er, sg + eg, sb + eb, sa + ea)
                .endVertex();
    }

    public static void drawColorTexLines(@Nonnull PoseStack poseStack, VertexConsumer builder, List<Vec2> points, int colorStart, int colorEnd, float width) {
        if (points.size() < 2) return;
        Matrix4f mat = poseStack.last().pose();
        Vec2 lastPoint = points.get(0);
        Vec2 point = points.get(1);
        Vector3f vec = null;
        int sa = (colorStart >> 24) & 0xff, sr = (colorStart >> 16) & 0xff, sg = (colorStart >> 8) & 0xff, sb = colorStart & 0xff;
        int ea = (colorEnd >> 24) & 0xff, er = (colorEnd >> 16) & 0xff, eg = (colorEnd >> 8) & 0xff, eb = colorEnd & 0xff;
        ea = (ea - sa);
        er = (er - sr);
        eg = (eg - sg);
        eb = (eb - sb);
        for (int i = 1; i < points.size(); i++) {
            float s = (i - 1f) / points.size();
            float e = i * 1f / points.size();
            point = points.get(i);
            float u = (i - 1f) / points.size();
            vec = new Vector3f(point.x - lastPoint.x, point.y - lastPoint.y, 0).rotateZ(Mth.HALF_PI).normalize().mul(-width);
            builder.vertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0).uv(u,0)
                    .color((sr + er * s) / 255, (sg + eg * s) / 255, (sb + eb * s) / 255, (sa + ea * s) / 255)
                    .endVertex();
            vec.mul(-1);
            builder.vertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0).uv(u,1)
                    .color((sr + er * e) / 255, (sg + eg * e) / 255, (sb + eb * e) / 255, (sa + ea * e) / 255)
                    .endVertex();
            lastPoint = point;
        }
        vec.mul(-1);
        builder.vertex(mat, point.x + vec.x, point.y + vec.y, 0).uv(1,0)
                .color(sr + er, sg + eg, sb + eb, sa + ea)
                .endVertex();
        vec.mul(-1);
        builder.vertex(mat, point.x + vec.x, point.y + vec.y, 0).uv(1,1)
                .color(sr + er, sg + eg, sb + eb, sa + ea)
                .endVertex();
    }


    /**
     *
     * cone
     *
     * @param poseStack  The stack used to store the transformation matrix.
     * @param buffer     Vertex consumer, which is used to cache vertex data.
     * @param x          The x coordinate of the center of the cone.
     * @param y          The y coordinate of the center of the cone.
     * @param z          The z coordinate of the center of the cone.
     * @param baseRadius The radius of the base of the cone.
     * @param height     The height of the cone.
     * @param segments   The number of subdivisions of the base.
     * @param red        color
     * @param green      color
     * @param blue       color
     * @param alpha      transparency
     * @param axis       The axial direction of the cone, which determines the direction of the cone.
     */
    public static void shapeCone(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z, float baseRadius,
                                 float height, int segments, float red, float green, float blue, float alpha,
                                 Direction.Axis axis) {
        Matrix4f mat = poseStack.last().pose();
        float segmentDelta = (float) (2.0 * Math.PI / segments); // Subdivision angle of the base
        float theta = 0; // θ, sin(θ), cos(θ) Base angle
        float cosTheta = 1.0F;
        float sinTheta = 0.0F;

        float nextCosTheta, nextSinTheta;

        // Base vertices
        for (int i = 0; i < segments; i++) {
            float theta1 = theta + segmentDelta;
            nextCosTheta = Mth.cos(theta1);
            nextSinTheta = Mth.sin(theta1);

            switch (axis) {
                case Y -> {
                    // Base of the cone
                    buffer.vertex(mat, x + cosTheta * baseRadius, y, z + sinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + nextCosTheta * baseRadius, y, z + nextSinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x, y + height, z)
                            .color(red, green, blue, alpha).endVertex();
                }
                case X -> {
                    buffer.vertex(mat, x, y + cosTheta * baseRadius, z + sinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x, y + nextCosTheta * baseRadius, z + nextSinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + height, y, z)
                            .color(red, green, blue, alpha).endVertex();
                }
                case Z -> {
                    buffer.vertex(mat, x + cosTheta * baseRadius, y + sinTheta * baseRadius, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + nextCosTheta * baseRadius, y + nextSinTheta * baseRadius, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x, y, z + height)
                            .color(red, green, blue, alpha).endVertex();
                }
            }

            theta = theta1;
            cosTheta = nextCosTheta;
            sinTheta = nextSinTheta;
        }
    }

    /**
     *
     * circle
     *
     * @param poseStack  The stack used to store the transformation matrix.
     * @param buffer     Vertex consumer, which is used to cache vertex data.
     * @param x          The x coordinate of the center of the cylinder.
     * @param y          The y coordinate of the center of the cylinder.
     * @param z          The z coordinate of the center of the cylinder.
     * @param baseRadius The radius of the base of the cylinder.
     * @param segments   The number of subdivisions of the base.
     * @param red        color
     * @param green      color
     * @param blue       color
     * @param alpha      transparency
     * @param axis       The axial direction of the cylinder, which determines the direction of the cylinder.
     */
    public static void shapeCircle(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z, float baseRadius,
                                   int segments, float red, float green, float blue, float alpha,
                                   Direction.Axis axis) {
        Matrix4f mat = poseStack.last().pose();
        float segmentDelta = (float) (2.0 * Math.PI / segments); // Subdivision angle of the base
        float theta = 0; // θ, sin(θ), cos(θ) Base angle
        float cosTheta = 1.0F;
        float sinTheta = 0.0F;

        float nextCosTheta, nextSinTheta;

        // Base vertices
        for (int i = 0; i < segments; i++) {
            float theta1 = theta + segmentDelta;
            nextCosTheta = Mth.cos(theta1);
            nextSinTheta = Mth.sin(theta1);

            switch (axis) {
                case Y -> {
                    // Base disk
                    buffer.vertex(mat, x, y, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + nextCosTheta * baseRadius, y, z + nextSinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + cosTheta * baseRadius, y, z + sinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                }
                case X -> {
                    buffer.vertex(mat, x, y, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x, y + nextCosTheta * baseRadius, z + nextSinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x, y + cosTheta * baseRadius, z + sinTheta * baseRadius)
                            .color(red, green, blue, alpha).endVertex();
                }
                case Z -> {
                    buffer.vertex(mat, x, y, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + nextCosTheta * baseRadius, y + nextSinTheta * baseRadius, z)
                            .color(red, green, blue, alpha).endVertex();
                    buffer.vertex(mat, x + cosTheta * baseRadius, y + sinTheta * baseRadius, z)
                            .color(red, green, blue, alpha).endVertex();
                }
            }

            theta = theta1;
            cosTheta = nextCosTheta;
            sinTheta = nextSinTheta;
        }
    }

    /**
     *
     * cube
     *
     * @param poseStack The stack used to store the transformation matrix.
     * @param buffer    Vertex consumer, which is used to cache vertex data.
     * @param x1        The x coordinate of the first corner of the cube.
     * @param y1        The y coordinate of the first corner of the cube.
     * @param z1        The z coordinate of the first corner of the cube.
     * @param x2        The x coordinate of the second corner of the cube.
     * @param y2        The y coordinate of the second corner of the cube.
     * @param z2        The z coordinate of the second corner of the cube.
     * @param red       color
     * @param green     color
     * @param blue      color
     * @param alpha     transparency
     */
    public static void shapeCube(PoseStack poseStack, VertexConsumer buffer, float x1, float y1, float z1,
                                  float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        Matrix4f mat = poseStack.last().pose();

        // Determine the min and max coordinates for each axis
        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);
        float minZ = Math.min(z1, z2);
        float maxZ = Math.max(z1, z2);

        // Define the 8 vertices of the cube
        float[][] vertices = {
                {minX, minY, minZ},
                {maxX, minY, minZ},
                {maxX, maxY, minZ},
                {minX, maxY, minZ},
                {minX, minY, maxZ},
                {maxX, minY, maxZ},
                {maxX, maxY, maxZ},
                {minX, maxY, maxZ}
        };

        // Define the 6 faces of the cube, each with 2 triangles (6 vertices)
        int[][] faces = {
                {0, 1, 2, 2, 3, 0}, // Front face
                {1, 5, 6, 6, 2, 1}, // Right face
                {5, 4, 7, 7, 6, 5}, // Back face
                {4, 0, 3, 3, 7, 4}, // Left face
                {3, 2, 6, 6, 7, 3}, // Top face
                {4, 5, 1, 1, 0, 4}  // Bottom face
        };

        // Iterate through each face and add the vertices
        for (int[] face : faces) {
            for (int index : face) {
                float[] vertex = vertices[index];
                buffer.vertex(mat, vertex[0], vertex[1], vertex[2]).color(red, green, blue, alpha).endVertex();
            }
        }
    }

    /**
     *
     * sphere
     *
     * @param poseStack The stack used to store the transformation matrix.
     * @param buffer    Vertex consumer, which is used to cache vertex data.
     * @param x         The x coordinate of the center of the sphere.
     * @param y         The y coordinate of the center of the sphere.
     * @param z         The z coordinate of the center of the sphere.
     * @param radius    The radius of the sphere.
     * @param stacks    The number of subdivisions of the latitude.
     * @param slices    The number of subdivisions of the longitude.
     * @param red       color
     * @param green     color
     * @param blue      color
     * @param alpha     transparency
     */
    public static void shapeSphere(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z, float radius,
                                    int stacks, int slices, float red, float green, float blue, float alpha) {
        Matrix4f mat = poseStack.last().pose();
        float stackStep = (float) Math.PI / stacks; // The step size between each stack (latitude)
        float sliceStep = (float) (2.0 * Math.PI / slices); // The step size between each slice (longitude)

        // Iterate through each stack
        for (int i = 0; i < stacks; i++) {
            float stackAngle1 = i * stackStep;
            float stackAngle2 = (i + 1) * stackStep;

            // Calculate the sin and cos for the stack angles
            float sinStack1 = (float) Math.sin(stackAngle1);
            float cosStack1 = (float) Math.cos(stackAngle1);
            float sinStack2 = (float) Math.sin(stackAngle2);
            float cosStack2 = (float) Math.cos(stackAngle2);

            // Iterate through each slice
            for (int j = 0; j < slices; j++) {
                float sliceAngle1 = j * sliceStep;
                float sliceAngle2 = (j + 1) * sliceStep;

                // Calculate the sin and cos for the slice angles
                float sinSlice1 = (float) Math.sin(sliceAngle1);
                float cosSlice1 = (float) Math.cos(sliceAngle1);
                float sinSlice2 = (float) Math.sin(sliceAngle2);
                float cosSlice2 = (float) Math.cos(sliceAngle2);

                // Define the 4 vertices of the current quad
                float[] v1 = {x + radius * sinStack1 * cosSlice1, y + radius * cosStack1, z + radius * sinStack1 * sinSlice1};
                float[] v2 = {x + radius * sinStack2 * cosSlice1, y + radius * cosStack2, z + radius * sinStack2 * sinSlice1};
                float[] v3 = {x + radius * sinStack2 * cosSlice2, y + radius * cosStack2, z + radius * sinStack2 * sinSlice2};
                float[] v4 = {x + radius * sinStack1 * cosSlice2, y + radius * cosStack1, z + radius * sinStack1 * sinSlice2};

                // First triangle
                buffer.vertex(mat, v1[0], v1[1], v1[2]).color(red, green, blue, alpha).endVertex();
                buffer.vertex(mat, v2[0], v2[1], v2[2]).color(red, green, blue, alpha).endVertex();
                buffer.vertex(mat, v3[0], v3[1], v3[2]).color(red, green, blue, alpha).endVertex();

                // Second triangle
                buffer.vertex(mat, v3[0], v3[1], v3[2]).color(red, green, blue, alpha).endVertex();
                buffer.vertex(mat, v4[0], v4[1], v4[2]).color(red, green, blue, alpha).endVertex();
                buffer.vertex(mat, v1[0], v1[1], v1[2]).color(red, green, blue, alpha).endVertex();
            }
        }
    }



}
