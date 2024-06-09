package com.lowdragmc.lowdraglib.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

/**
 * @author KilaBash
 * @date 2022/05/28
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow protected abstract void registerModelAndLoadDependencies(ModelResourceLocation location, UnbakedModel model);

    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation location) throws IOException;

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/client/resources/model/BlockStateModelLoader"))
    protected BlockStateModelLoader ldlib$captureModelBakery(BlockStateModelLoader original) {
        ModelFactory.setModelBakery((ModelBakery) (Object) this);
        //noinspection UnreachableCode the above cast works fine. the IDE just doesn't know that.
        return original;
    }

    /**
     * avoid warning
     */
    @Redirect(method = "getModel",
            at = @At(value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    @SuppressWarnings("mapping")
    protected void ldlib$injectStateToModelLocation(Logger logger, String string, Object[] objects) {
        String location = objects[0].toString();
        if (location.endsWith("#inventory") && BuiltInRegistries.ITEM.get(ResourceLocation.parse(location.substring(0, location.length() - "#inventory".length()))) instanceof IItemRendererProvider) {
            return;
        }
        logger.warn(location, objects);
    }

    @Inject(method = "loadBlockModel",
            at = @At(value = "HEAD"), cancellable = true)
    protected void ldlib$injectLoadModel(ResourceLocation pLocation, CallbackInfoReturnable<BlockModel> cir) throws IOException {
        if (pLocation.getPath().startsWith("block/")) {
            pLocation = pLocation.withPath(pLocation.getPath().substring("block/".length()));
        } else if (pLocation.getPath().startsWith("item/")) {
            pLocation = pLocation.withPath(pLocation.getPath().substring("item/".length()));
        }
        var block = BuiltInRegistries.BLOCK.get(pLocation);
        if (block instanceof IBlockRendererProvider) {
            BlockModel model = loadBlockModel(ResourceLocation.fromNamespaceAndPath(LDLib.MOD_ID, "block/renderer_model"));
            cir.setReturnValue(model);
        }
    }
}
