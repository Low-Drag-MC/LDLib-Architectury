package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote TODO
 */
@Mixin(BlockModelShaper.class)
public abstract class BlockModelShaperMixin {
    @Inject(method = "stateToModelLocation(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/ModelResourceLocation;", at = @At(value = "HEAD"), cancellable = true)
    private static void injectStateToModelLocation(ResourceLocation pLocation, BlockState pState, CallbackInfoReturnable<ModelResourceLocation> cir) {
        if (pState.getBlock() instanceof IBlockRendererProvider) {
            cir.setReturnValue(new ModelResourceLocation(new ResourceLocation("ldlib:renderer_model"), ""));
        }
    }
}
