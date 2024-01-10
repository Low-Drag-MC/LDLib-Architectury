package com.lowdragmc.lowdraglib.client.utils;

import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderBufferUtils {

    public static void renderCubeFrame(PoseStack poseStack, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
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

}
