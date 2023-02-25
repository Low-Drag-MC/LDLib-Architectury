package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2023/2/23
 * @implNote TerrainParticle
 */
@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
    @Inject(method = "getParticleIcon",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void reloadShaders(BlockState state, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (state.getBlock() instanceof IBlockRendererProvider blockRendererProvider) {
            var renderer = blockRendererProvider.getRenderer(state);
            if (renderer != null) {
                cir.setReturnValue(renderer.getParticleTexture());
            }
        }
    }
}
