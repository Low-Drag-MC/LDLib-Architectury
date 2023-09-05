package com.lowdragmc.lowdraglib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

import java.nio.file.Path;

public class Platform {

    @ApiStatus.Internal
    public static RegistryAccess FROZEN_REGISTRY_ACCESS;

    @ExpectPlatform
    public static String platformName() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isForge() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevEnv() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDatagen() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Nullable
    public static MinecraftServer getMinecraftServer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGamePath() {
        throw new AssertionError();
    }

    @Nullable
    public static RegistryAccess getFrozenRegistry() {
        return FROZEN_REGISTRY_ACCESS;
    }

}
