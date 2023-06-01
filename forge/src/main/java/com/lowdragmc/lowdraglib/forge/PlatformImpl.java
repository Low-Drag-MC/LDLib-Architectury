package com.lowdragmc.lowdraglib.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FMLServiceProvider;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class PlatformImpl {
	public static String platformName() {
		return "Forge";
	}

	public static boolean isForge() {
		return true;
	}

	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

    public static boolean isDatagen() {
        return ModLoader.isDataGenRunning();
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

}
