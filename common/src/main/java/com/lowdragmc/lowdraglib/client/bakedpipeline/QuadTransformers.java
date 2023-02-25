package com.lowdragmc.lowdraglib.client.bakedpipeline;

import com.google.common.base.Preconditions;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;

import java.util.Arrays;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote QuadTransformers
 */
public class QuadTransformers {
    private static final IQuadTransformer EMPTY = (quad) -> {
    };
    private static final IQuadTransformer[] EMISSIVE_TRANSFORMERS = Util.make(new IQuadTransformer[16], (array) ->
            Arrays.setAll(array, (i) -> applyingLightmap(LightTexture.pack(i, i)))
    );

    public static IQuadTransformer empty() {
        return EMPTY;
    }

    public static IQuadTransformer applyingLightmap(int lightmap) {
        return (quad) -> {
            int[] vertices = quad.getVertices();

            for(int i = 0; i < 4; ++i) {
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.UV2] = lightmap;
            }

        };
    }

    public static IQuadTransformer settingEmissivity(int emissivity) {
        Preconditions.checkArgument(emissivity >= 0 && emissivity < 16, "Emissivity must be between 0 and 15.");
        return EMISSIVE_TRANSFORMERS[emissivity];
    }

    public static IQuadTransformer settingMaxEmissivity() {
        return EMISSIVE_TRANSFORMERS[15];
    }

    private QuadTransformers() {
    }
}
