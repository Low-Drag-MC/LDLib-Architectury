package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.model.fabric.ModelFactoryImpl;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ModelManagerMixin
 */
@Mixin(ModelBakery.class)
public abstract class ModelManagerMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <T extends BlockEntity> void injectPrepare(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
        ModelFactoryImpl.BAKERY = ModelBakery.class.cast(this);
    }

}
