package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote MinecraftServerMixin
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @ModifyVariable(method = "stopServer", at = @At(value= "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;close()V", ordinal = 0))
    public ServerLevel MinecraftServer_stopServer(ServerLevel serverLevel) {
        AsyncThreadData.getOrCreate(serverLevel).releaseExecutorService();
        return serverLevel;
    }

}
