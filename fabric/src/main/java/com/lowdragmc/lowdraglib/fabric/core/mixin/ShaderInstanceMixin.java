package com.lowdragmc.lowdraglib.fabric.core.mixin;

import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote ShaderInstanceMixin,  inject custom shader config to vanilla shader configs.
 * Ensure the shader is loading from the correct resource location. Fabric ignores the ResourceLocation path passed to this method
 */
@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"), index = 0)
    private static String injectInit(String string) {
        if (string.startsWith("shaders/core/%s:".formatted(LDLib.MOD_ID))) {
            return LDLib.MOD_ID + ":" + string.replace(LDLib.MOD_ID + ":", "");
        }
        return string;
    }


    @ModifyArg(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/FileUtil;getFullResourcePath(Ljava/lang/String;)Ljava/lang/String;"), index = 0)
    private static String injectFile(String string) {
        if (string.startsWith("shaders/core/%s:".formatted(LDLib.MOD_ID))) {
            return LDLib.MOD_ID + ":" + string.replace(LDLib.MOD_ID + ":", "");
        }
        return string;
    }

    @ModifyArg(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"), index = 0)
    private static String injectCreate(String string) {
        if (string.startsWith("shaders/core/%s:".formatted(LDLib.MOD_ID))) {
            return LDLib.MOD_ID + ":" + string.replace(LDLib.MOD_ID + ":", "");
        }
        return string;
    }

}
