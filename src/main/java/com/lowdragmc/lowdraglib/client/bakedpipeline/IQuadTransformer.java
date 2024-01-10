package com.lowdragmc.lowdraglib.client.bakedpipeline;

import com.lowdragmc.lowdraglib.core.mixins.accessor.VertexFormatAccessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.Arrays;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote IQuadTransformer
 */
public interface IQuadTransformer {
    int STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();
    int POSITION = findOffset(DefaultVertexFormat.ELEMENT_POSITION);
    int COLOR = findOffset(DefaultVertexFormat.ELEMENT_COLOR);
    int UV0 = findOffset(DefaultVertexFormat.ELEMENT_UV0);
    int UV1 = findOffset(DefaultVertexFormat.ELEMENT_UV1);
    int UV2 = findOffset(DefaultVertexFormat.ELEMENT_UV2);
    int NORMAL = findOffset(DefaultVertexFormat.ELEMENT_NORMAL);

    void processInPlace(BakedQuad arg);

    default void processInPlace(List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            this.processInPlace(quad);
        }

    }

    default BakedQuad process(BakedQuad quad) {
        BakedQuad copy = copy(quad);
        this.processInPlace(copy);
        return copy;
    }

    default List<BakedQuad> process(List<BakedQuad> inputs) {
        return inputs.stream().map(IQuadTransformer::copy).peek(this::processInPlace).toList();
    }

    default IQuadTransformer andThen(IQuadTransformer other) {
        return (quad) -> {
            this.processInPlace(quad);
            other.processInPlace(quad);
        };
    }

    /** @deprecated */
    @Deprecated(
            forRemoval = true,
            since = "1.19"
    )
    static IQuadTransformer empty() {
        return QuadTransformers.empty();
    }


    /** @deprecated */
    @Deprecated(
            forRemoval = true,
            since = "1.19"
    )
    static IQuadTransformer applyingLightmap(int lightmap) {
        return QuadTransformers.applyingLightmap(lightmap);
    }

    private static BakedQuad copy(BakedQuad quad) {
        int[] vertices = quad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    private static int findOffset(VertexFormatElement element) {
        int index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : ((VertexFormatAccessor)DefaultVertexFormat.BLOCK).getOffsets().getInt(index) / 4;
    }
}
