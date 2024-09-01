package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.utils.SpriteFunctionWrapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Function;

@Mixin(targets = {"net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl"})
public abstract class ModelBakerImplMixin {

    // ít's assuming we want the forge method, so surpress
    @SuppressWarnings({ "MixinAnnotationTarget", "InvalidInjectorMethodSignature" })
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/UnbakedModel;bake(Lnet/minecraft/client/resources/model/ModelBaker;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/BakedModel;"),
               method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;")
    private Function<Material, TextureAtlasSprite> ldlib$scrapeTextures(ModelBaker baker, Function<Material, TextureAtlasSprite> function, ModelState transform, ResourceLocation modelLocation) {
        return new SpriteFunctionWrapper(function, modelLocation);
    }
}
