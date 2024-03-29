package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote GameRendererMixin, used to refresh shader and fbo size.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final private Map<String, ShaderInstance> shaders;

    /* Replacement for RegisterShadersEvent, as fabric has no equivalent event  */
    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V", shift = At.Shift.AFTER))
    private void reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        this.setupShader(Shaders::registerShaders, resourceProvider);
    }

	private void setupShader(Function<ResourceProvider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>>> function, ResourceProvider resourceProvider){
		var shaders = function.apply(resourceProvider);
        for (Pair<ShaderInstance, Consumer<ShaderInstance>> shader : shaders) {
            this.shaders.put(shader.getFirst().getName(),shader.getFirst());
            shader.getSecond().accept(shader.getFirst());
        }
	}

}
