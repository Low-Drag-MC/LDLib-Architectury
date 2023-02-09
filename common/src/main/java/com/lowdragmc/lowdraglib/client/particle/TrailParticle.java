package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote TrailParticle
 */
@Environment(EnvType.CLIENT)
public abstract class TrailParticle extends LParticle {
    @Getter
    protected ArrayList<Vector3> tails = new ArrayList<>();
    @Setter @Getter
    protected int maxTail;
    @Setter @Getter
    protected int freq;
    @Setter @Getter
    protected float width;
    public boolean removeTail;

    protected TrailParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        maxTail = 40;
        freq = 1;
        width = 0.5f;
        cull = false;
        removeTail = true;
    }

    protected TrailParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
        cull = false;
    }

    protected boolean shouldAddTail(Vector3 newTail) {
        return true;
    }

    @Override
    public final void tick() {
        if (delay > 0) {
            delay--;
            return;
        }
        if (age % freq == 0) {
            Vector3 tail = getTail();
            if (shouldAddTail(tail)) {
                tails.add(tail);
            }
            if (removeTail) {
                while (tails.size() > maxTail) {
                    tails.remove(0);
                }
            }
        }
        super.tick();
    }

    protected Vector3 getTail() {
        return new Vector3(this.xo, this.yo, this.zo);
    }

    public void renderInternal(@Nonnull VertexConsumer buffer, @Nonnull Camera camera, float partialTicks) {
        Vector3[] verts = new Vector3[tails.size() * 2];
        double x = (Mth.lerp(partialTicks, this.xo, this.x));
        double y = (Mth.lerp(partialTicks, this.yo, this.y));
        double z = (Mth.lerp(partialTicks, this.zo, this.z));

        float a = getAlpha(partialTicks);
        float r = getRed(partialTicks);
        float g = getGreen(partialTicks);
        float b = getBlue(partialTicks);
        
        Vector3 lastTail = new Vector3(x, y, z);
        Vector3 cameraPos = new Vector3(camera.getPosition());
        int size = tails.size() - 1;
        for (int i = size; i >= 0; i--) {
            Vector3 tail = new Vector3(tails.get(i));
            Vector3 nextTail = tail;
            if (i - 1 > 0) {
                nextTail = new Vector3(tails.get(i - 1));
            }
            renderTail(verts,size - i, cameraPos, lastTail, tail, nextTail, partialTicks);
            lastTail = tail;
        }
        for (int i = 0; i < (verts.length / 2) - 1; i++) {
            Vector3 currentU = verts[i * 2];
            Vector3 currentD = verts[i * 2 + 1];
            Vector3 nextU = verts[(i + 1) * 2];
            Vector3 nextD = verts[(i + 1) * 2 + 1];

            float u0 = getU0(i, partialTicks);
            float u1 = getU1(i, partialTicks);
            float v0 = getV0(i, partialTicks);
            float v1 = getV1(i, partialTicks);
            int light = getLightColor(i, partialTicks);

            buffer.vertex(currentD.x, currentD.y, currentD.z).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
            buffer.vertex(currentU.x, currentU.y, currentU.z).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
            buffer.vertex(nextD.x, nextD.y, nextD.z).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();

            buffer.vertex(nextD.x, nextD.y, nextD.z).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
            buffer.vertex(currentU.x, currentU.y, currentU.z).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
            buffer.vertex(nextU.x, nextU.y, nextU.z).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
        }
    }

    public void renderTail(Vector3[] verts, int i, Vector3 cameraPos, Vector3 pre, Vector3 current, Vector3 nextTail, float partialTicks) {
        float size = getWidth(i, partialTicks);
        Vector3 toTail = current.copy().subtract(cameraPos);
        Vector3 preNormal = toTail.copy().crossProduct(current.copy().subtract(pre)).normalize();
        Vector3 nextNormal = toTail.copy().crossProduct(nextTail.copy().subtract(current)).normalize();
        verts[i * 2] = current.copy().add(preNormal.copy().multiply(size)).subtract(cameraPos);
        verts[i * 2 + 1] = current.copy().add(nextNormal.copy().multiply(-size)).subtract(cameraPos);
    }

    public float getWidth(int tail, float pPartialTicks) {
        return width;
    }

    public int getLightColor(int tail, float pPartialTicks) {
        return getLight(pPartialTicks);
    }

    protected float getU0(int tail, float pPartialTicks) {
        return  1 - (tail + 1 + pPartialTicks) / (maxTail - 1f);
    }

    protected float getV0(int tail, float pPartialTicks) {
        return 0;
    }

    protected float getU1(int tail, float pPartialTicks) {
        return  1 - (tail + pPartialTicks) / (maxTail - 1f);
    }

    protected float getV1(int tail, float pPartialTicks) {
        return 1;
    }

    @Override
    protected final float getU0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getU1(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getV0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected final float getV1(float pPartialTicks) {
        return 0;
    }
}
