package com.lowdragmc.lowdraglib.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO remove once we get a version that doesn't delete all the quad data
// exists so I can get a screenshot of a machine without it being invisible
@Mixin(value = QuadBakingVertexConsumer.class, remap = false)
public class QuadBakingVertexConsumerMixin {

    @WrapOperation(method = "bakeQuad", at = @At(value = "NEW", target = "net/minecraft/client/renderer/block/model/BakedQuad"))
    private BakedQuad gtceu$modifyQuad(int[] pVertices, int pTintIndex,
                                       Direction pDirection, TextureAtlasSprite pSprite,
                                       boolean pShade, boolean hasAmbientOcclusion,
                                       Operation<BakedQuad> original) {
        return original.call(pVertices.clone(), pTintIndex, pDirection, pSprite, pShade, hasAmbientOcclusion);
    }
}
