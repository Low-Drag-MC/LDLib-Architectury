package com.lowdragmc.lowdraglib.client.bakedpipeline;

import com.google.common.base.Preconditions;
import com.lowdragmc.lowdraglib.core.mixins.accessor.VertexFormatAccessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

/**
 * @author KilaBash
 * @date 2023/3/23
 * @implNote QuadBakingVertexConsumer
 */
public class QuadBakingVertexConsumer implements VertexConsumer {
    private final Map<VertexFormatElement, Integer> ELEMENT_OFFSETS = Util.make(new IdentityHashMap<>(), (map) -> {
        int i = 0;
        for (VertexFormatElement element : DefaultVertexFormat.BLOCK.getElements()) {
            map.put(element, ((VertexFormatAccessor)DefaultVertexFormat.BLOCK).getOffsets().getInt(i++) / 4);
        }
    });

    private static final int QUAD_DATA_SIZE;
    private final Consumer<BakedQuad> quadConsumer;
    int vertexIndex = 0;
    private int[] quadData;
    private int tintIndex;
    private Direction direction;
    private TextureAtlasSprite sprite;
    private boolean shade;
    private boolean hasAmbientOcclusion;

    public QuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer) {
        this.quadData = new int[QUAD_DATA_SIZE];
        this.direction = Direction.DOWN;
        this.sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
        this.quadConsumer = quadConsumer;
    }

    public VertexConsumer vertex(double x, double y, double z) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        this.quadData[offset] = Float.floatToRawIntBits((float)x);
        this.quadData[offset + 1] = Float.floatToRawIntBits((float)y);
        this.quadData[offset + 2] = Float.floatToRawIntBits((float)z);
        return this;
    }

    public VertexConsumer normal(float x, float y, float z) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        this.quadData[offset] = (int)(x * 127.0F) & 255 | ((int)(y * 127.0F) & 255) << 8 | ((int)(z * 127.0F) & 255) << 16;
        return this;
    }

    public VertexConsumer color(int red, int green, int blue, int alpha) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        this.quadData[offset] = (alpha & 255) << 24 | (blue & 255) << 16 | (green & 255) << 8 | red & 255;
        return this;
    }

    public VertexConsumer uv(float u, float v) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        this.quadData[offset] = Float.floatToRawIntBits(u);
        this.quadData[offset + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    public VertexConsumer overlayCoords(int u, int v) {
        if (IQuadTransformer.UV1 >= 0) {
            int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV1;
            this.quadData[offset] = u & '\uffff' | (v & '\uffff') << 16;
        }

        return this;
    }

    public VertexConsumer uv2(int u, int v) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        this.quadData[offset] = u & '\uffff' | (v & '\uffff') << 16;
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int... rawData) {
        Integer baseOffset = (Integer)this.ELEMENT_OFFSETS.get(element);
        if (baseOffset != null) {
            int offset = this.vertexIndex * IQuadTransformer.STRIDE + baseOffset;
            System.arraycopy(rawData, 0, this.quadData, offset, rawData.length);
        }

        return this;
    }

    public void endVertex() {
        if (++this.vertexIndex == 4) {
            this.quadConsumer.accept(new BakedQuad(this.quadData, this.tintIndex, this.direction, this.sprite, this.shade));
            this.vertexIndex = 0;
            this.quadData = new int[QUAD_DATA_SIZE];
        }
    }

    public void defaultColor(int defaultR, int defaultG, int defaultB, int defaultA) {
    }

    public void unsetDefaultColor() {
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public void setShade(boolean shade) {
        this.shade = shade;
    }

    public void setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
    }

    static {
        QUAD_DATA_SIZE = IQuadTransformer.STRIDE * 4;
    }

    public static class Buffered extends QuadBakingVertexConsumer {
        private final BakedQuad[] output;

        public Buffered() {
            this(new BakedQuad[1]);
        }

        private Buffered(BakedQuad[] output) {
            super((q) -> output[0] = q);
            this.output = output;
        }

        public BakedQuad getQuad() {
            BakedQuad quad = Preconditions.checkNotNull(this.output[0], "No quad has been emitted. Vertices in buffer: " + this.vertexIndex);
            this.output[0] = null;
            return quad;
        }
    }
}
