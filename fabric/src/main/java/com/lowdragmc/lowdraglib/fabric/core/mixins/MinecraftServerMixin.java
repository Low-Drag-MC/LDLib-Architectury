package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote MinecraftServerMixin
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Redirect(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;close()V"))
    private void injectPrepare(ServerLevel serverLevel) throws IOException {
        AsyncThreadData.getOrCreate(serverLevel).releaseExecutorService();
        serverLevel.close();
    }

}
