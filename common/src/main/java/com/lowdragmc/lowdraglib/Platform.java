package com.lowdragmc.lowdraglib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class Platform {

    private static final RegistryAccess BLANK = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    // This is a helper method to check if the ServerLevel is safe to access.
    // @return true if the ServerLevel is not safe to access, otherwise false.
    public static boolean isNotSafe() {
        if (Platform.isClient()) {
            return Minecraft.getInstance().getConnection() == null;
        } else {
            var server = getMinecraftServer();
            return server == null || server.isStopped() || server.isShutdown() || !server.isRunning() || server.isCurrentlySaving();
        }
    }
    @ApiStatus.Internal
    public static RegistryAccess FROZEN_REGISTRY_ACCESS = BLANK;

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

    public static RegistryAccess getFrozenRegistry() {
        if (FROZEN_REGISTRY_ACCESS != null) {
            return FROZEN_REGISTRY_ACCESS;
        } else if (LDLib.isRemote()) {
            if (Minecraft.getInstance().getConnection() != null) {
                return Minecraft.getInstance().getConnection().registryAccess();
            }
        }
        return BLANK;
    }

}
