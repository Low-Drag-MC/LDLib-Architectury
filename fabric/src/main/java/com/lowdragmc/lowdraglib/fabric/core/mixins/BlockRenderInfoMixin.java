package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote BlockRenderInfoMixin
 */
@Mixin(BlockRenderInfo.class)
public class BlockRenderInfoMixin {
    @Shadow(remap = false) boolean defaultAo;

    /**
     * Hooks ao according to IRenderer
     */
    @Inject(method = "prepareForBlock", at = @At(value = "RETURN"))
    private void injectPrepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAO, CallbackInfo ci) {
        if (blockState.getBlock() instanceof IBlockRendererProvider blockRendererProvider) {
            var renderer = blockRendererProvider.getRenderer(blockState);
            if (renderer != null) {
                this.defaultAo = renderer.useAO(blockState) && Minecraft.useAmbientOcclusion() && blockState.getLightEmission() == 0;
            }
        }
    }
}
