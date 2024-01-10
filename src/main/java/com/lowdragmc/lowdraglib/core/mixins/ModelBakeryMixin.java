package com.lowdragmc.lowdraglib.core.mixins;

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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

/**
 * @author KilaBash
 * @date 2022/05/28
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation location, UnbakedModel model);

    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation location) throws IOException;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    /**
     * avoid warning
     */
    @Redirect(method = "getModel",
            at = @At(value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    @SuppressWarnings("mapping")
    protected void injectStateToModelLocation(Logger logger, String string, Object[] objects) {
        String location = objects[0].toString();
        if (location.endsWith("#inventory") && BuiltInRegistries.ITEM.get(new ResourceLocation(location.substring(0, location.length() - "#inventory".length()))) instanceof IItemRendererProvider) {
            return;
        }
        logger.warn(location, objects);
    }

    @Inject(method = "loadModel",
            at = @At(value = "HEAD"), cancellable = true)
    protected void injectLoadModel(ResourceLocation blockstateLocation, CallbackInfo ci) {
        if (blockstateLocation instanceof ModelResourceLocation modelResourceLocation) {
            if (!Objects.equals(modelResourceLocation.getVariant(), "inventory")) {
                ResourceLocation resourceLocation = new ResourceLocation(blockstateLocation.getNamespace(), blockstateLocation.getPath());
                var block = BuiltInRegistries.BLOCK.get(resourceLocation);
                if (block instanceof IBlockRendererProvider) {
                    var model = this.unbakedCache.computeIfAbsent(new ResourceLocation("ldlib:block/renderer_model"), modelLocation -> {
                        try {
                            return ModelFactory.getLDLibModel(loadBlockModel(modelLocation));
                        } catch (IOException e) {
                            LDLib.LOGGER.error("Couldn't load ldlib:renderer_model", e);
                            return this.unbakedCache.get(MISSING_MODEL_LOCATION);
                        }
                    });
                    this.cacheAndQueueDependencies(modelResourceLocation, model);
                    ci.cancel();
                }
            }
        }

    }
}
