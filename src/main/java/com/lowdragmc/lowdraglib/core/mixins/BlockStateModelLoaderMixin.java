package com.lowdragmc.lowdraglib.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {

    @Shadow @Final private BlockColors blockColors;

    @ModifyExpressionValue(method = "lambda$loadBlockStateDefinitions$10", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private Object ldlib$injectStateLoading(Object original, @Local(ordinal = 0, argsOnly = true) BlockState blockState) {
        if (blockState.getBlock() instanceof IBlockRendererProvider) {
            ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(LDLib.MOD_ID, "block/renderer_model");
            UnbakedModel model = ModelFactory.getUnBakedModel(modelPath);
            return new BlockStateModelLoader.LoadedModel(model,
                    () -> BlockStateModelLoader.ModelGroupKey.create(blockState, model, this.blockColors.getColoringProperties(blockState.getBlock())));
        }
        return original;
    }
}
