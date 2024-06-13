package com.lowdragmc.lowdraglib.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

/**
 * @author KilaBash
 * @date 2022/05/28
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow abstract UnbakedModel getModel(ResourceLocation modelPath);

    @ModifyExpressionValue(method = "registerModelAndLoadDependencies",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/client/resources/model/UnbakedModel;getDependencies()Ljava/util/Collection;"))
    protected Collection<ResourceLocation> ldlib$changeLoadedModel(Collection<ResourceLocation> original,
                                                                   @Local(argsOnly = true) ModelResourceLocation modelResourceLocation,
                                                                   @Local(argsOnly = true) LocalRef<UnbakedModel> model) {
        if (!modelResourceLocation.getVariant().equals(ModelResourceLocation.STANDALONE_VARIANT)) {
            ResourceLocation resourceLocation = modelResourceLocation.id();
            var block = BuiltInRegistries.BLOCK.get(resourceLocation);
            if (block instanceof IBlockRendererProvider) {
                UnbakedModel newModel = getModel(LDLib.location("block/renderer_model"));
                model.set(newModel);
                return newModel.getDependencies();
            }
        }
        return original;
    }
}
