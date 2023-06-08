package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author KilaBash
 * @date 2022/05/28
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

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
}
