package com.lowdragmc.lowdraglib.client.utils;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a).setNormal(1,0,0);
        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a).setNormal(1,0,0);

        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a).setNormal(0,1,0);
        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a).setNormal(0,1,0);

        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a).setNormal(0,0,1);
        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a).setNormal(0,0,1);

        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(1,0,0);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(1,0,0);

        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(0,1,0);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(0,1,0);

        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(0,0,1);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(0,0,1);

        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a).setNormal(0,0,1);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(0,0,1);

        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a).setNormal(1,0,0);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(1,0,0);

        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a).setNormal(0,0,1);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(0,0,1);

        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a).setNormal(0,1,0);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(0,1,0);

        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a).setNormal(1,0,0);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(1,0,0);

        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a).setNormal(0,1,0);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(0,1,0);
    }

    public static void renderCubeFace(PoseStack poseStack, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float a, boolean shade) {
        Matrix4f mat = poseStack.last().pose();
        float r = red, g = green, b = blue;

        if (shade) {
            r *= 0.6;
            g *= 0.6;
            b *= 0.6;
        }
        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a);

        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a);

        if (shade) {
            r = red * 0.5f;
            g = green * 0.5f;
            b = blue * 0.5f;
        }
        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a);

        if (shade) {
            r = red;
            g = green;
            b = blue;
        }
        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a);

        if (shade) {
            r = red * 0.8f;
            g = green * 0.8f;
            b = blue * 0.8f;
        }
        buffer.addVertex(mat, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, minY, minZ).setColor(r, g, b, a);

        buffer.addVertex(mat, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(r, g, b, a);
    }

    public static void renderCubeFace(PoseStack poseStack, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int setColor, int combinedLight, TextureAtlasSprite textureSprite) {
        Matrix4f mat = poseStack.last().pose();
        PoseStack.Pose last = poseStack.last();
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();

        buffer.addVertex(mat, minX, minY, minZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, -1, 0, 0);
        buffer.addVertex(mat, minX, minY, maxZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, -1, 0, 0);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, -1, 0, 0);
        buffer.addVertex(mat, minX, maxY, minZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, -1, 0, 0);

        buffer.addVertex(mat, maxX, minY, minZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 1, 0, 0);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 1, 0, 0);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 1, 0, 0);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 1, 0, 0);


        buffer.addVertex(mat, minX, minY, minZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, -1, 0);
        buffer.addVertex(mat, maxX, minY, minZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, -1, 0);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, -1, 0);
        buffer.addVertex(mat, minX, minY, maxZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, -1, 0);


        buffer.addVertex(mat, minX, maxY, minZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 1, 0);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 1, 0);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 1, 0);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 1, 0);

        buffer.addVertex(mat, minX, minY, minZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, -1);
        buffer.addVertex(mat, minX, maxY, minZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, -1);
        buffer.addVertex(mat, maxX, maxY, minZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, -1);
        buffer.addVertex(mat, maxX, minY, minZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, -1);

        buffer.addVertex(mat, minX, minY, maxZ).setColor(setColor).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, 1);
        buffer.addVertex(mat, maxX, minY, maxZ).setColor(setColor).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, 1);
        buffer.addVertex(mat, maxX, maxY, maxZ).setColor(setColor).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, 1);
        buffer.addVertex(mat, minX, maxY, maxZ).setColor(setColor).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, 0, 0, 1);
    }

    public static void drawColorLines(@Nonnull PoseStack poseStack, VertexConsumer builder, List<Vec2> points, int setColorStart, int setColorEnd, float width) {
        if (points.size() < 2) return;
        Matrix4f mat = poseStack.last().pose();
        Vec2 lastPoint = points.get(0);
        Vec2 point = points.get(1);
        Vector3f vec = null;
        int sa = (setColorStart >> 24) & 0xff, sr = (setColorStart >> 16) & 0xff, sg = (setColorStart >> 8) & 0xff, sb = setColorStart & 0xff;
        int ea = (setColorEnd >> 24) & 0xff, er = (setColorEnd >> 16) & 0xff, eg = (setColorEnd >> 8) & 0xff, eb = setColorEnd & 0xff;
        ea = (ea - sa);
        er = (er - sr);
        eg = (eg - sg);
        eb = (eb - sb);
        for (int i = 1; i < points.size(); i++) {
            float s = (i - 1f) / points.size();
            float e = i * 1f / points.size();
            point = points.get(i);
            vec = new Vector3f(point.x - lastPoint.x, point.y - lastPoint.y, 0).rotateZ(Mth.HALF_PI).normalize().mul(-width);
            builder.addVertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0)
                    .setColor((sr + er * s) / 255, (sg + eg * s) / 255, (sb + eb * s) / 255, (sa + ea * s) / 255)
                    ;
            vec.mul(-1);
            builder.addVertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0)
                    .setColor((sr + er * e) / 255, (sg + eg * e) / 255, (sb + eb * e) / 255, (sa + ea * e) / 255)
                    ;
            lastPoint = point;
        }
        vec.mul(-1);
        builder.addVertex(mat, point.x + vec.x, point.y + vec.y, 0)
                .setColor(sr + er, sg + eg, sb + eb, sa + ea)
                ;
        vec.mul(-1);
        builder.addVertex(mat, point.x + vec.x, point.y + vec.y, 0)
                .setColor(sr + er, sg + eg, sb + eb, sa + ea)
                ;
    }

    public static void drawColorTexLines(@Nonnull PoseStack poseStack, VertexConsumer builder, List<Vec2> points, int setColorStart, int setColorEnd, float width) {
        if (points.size() < 2) return;
        Matrix4f mat = poseStack.last().pose();
        Vec2 lastPoint = points.get(0);
        Vec2 point = points.get(1);
        Vector3f vec = null;
        int sa = (setColorStart >> 24) & 0xff, sr = (setColorStart >> 16) & 0xff, sg = (setColorStart >> 8) & 0xff, sb = setColorStart & 0xff;
        int ea = (setColorEnd >> 24) & 0xff, er = (setColorEnd >> 16) & 0xff, eg = (setColorEnd >> 8) & 0xff, eb = setColorEnd & 0xff;
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
            builder.addVertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0).setUv(u,0)
                    .setColor((sr + er * s) / 255, (sg + eg * s) / 255, (sb + eb * s) / 255, (sa + ea * s) / 255)
                    ;
            vec.mul(-1);
            builder.addVertex(mat, lastPoint.x + vec.x, lastPoint.y + vec.y, 0).setUv(u,1)
                    .setColor((sr + er * e) / 255, (sg + eg * e) / 255, (sb + eb * e) / 255, (sa + ea * e) / 255)
                    ;
            lastPoint = point;
        }
        vec.mul(-1);
        builder.addVertex(mat, point.x + vec.x, point.y + vec.y, 0).setUv(1,0)
                .setColor(sr + er, sg + eg, sb + eb, sa + ea)
                ;
        vec.mul(-1);
        builder.addVertex(mat, point.x + vec.x, point.y + vec.y, 0).setUv(1,1)
                .setColor(sr + er, sg + eg, sb + eb, sa + ea)
                ;
    }

}
