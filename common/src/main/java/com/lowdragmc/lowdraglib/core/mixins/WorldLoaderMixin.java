package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.Platform;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <D, R> void ldlib$getRegistryAccess(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> loadContextSupplier, WorldLoader.ResultFactory<D, R> applierFactory, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<R>> cir,
                                                       Pair<WorldDataConfiguration, CloseableResourceManager> pair, CloseableResourceManager closeableResourceManager, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess2, RegistryAccess.Frozen frozen, RegistryAccess.Frozen frozen2, WorldDataConfiguration worldDataConfiguration, WorldLoader.DataLoadOutput<D> dataLoadOutput, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess3,
                                                       RegistryAccess.Frozen frozen3) {
        Platform.FROZEN_REGISTRY_ACCESS = frozen3;
    }
}
