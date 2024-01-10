package com.lowdragmc.lowdraglib;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

import java.nio.file.Path;

public class Platform {

    @ApiStatus.Internal
    public static RegistryAccess FROZEN_REGISTRY_ACCESS;


    public static String platformName() {
        return "NeoForge";
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isDevEnv() {
        return !FMLLoader.isProduction();
    }

    public static boolean isDatagen() {
        return FMLLoader.getLaunchHandler().isData();
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.get();
    }

    @Nullable
    public static RegistryAccess getFrozenRegistry() {
        return FROZEN_REGISTRY_ACCESS;
    }

}
