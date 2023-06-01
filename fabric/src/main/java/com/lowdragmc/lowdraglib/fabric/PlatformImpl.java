package com.lowdragmc.lowdraglib.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.nio.file.Path;

public class PlatformImpl {

	protected static MinecraftServer SERVER;

	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}

	public static boolean isForge() {
		return false;
	}

	public static boolean isDevEnv() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean isDatagen() {
		return FabricDataGenHelper.ENABLED;
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static MinecraftServer getMinecraftServer() {
		return SERVER;
	}

	public static Path getGamePath() {
		return FabricLoader.getInstance().getGameDir();
	}

}
