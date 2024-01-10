package com.lowdragmc.lowdraglib.core.mixins;

public interface MixinPluginShared {

	static boolean isClassFound(String className) {
		try {
			Class.forName(className, false, Thread.currentThread().getContextClassLoader());
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	boolean IS_OPT_LOAD = isClassFound("optifine.OptiFineTranformationService");
	boolean IS_DASH_LOADER = isClassFound("dev.quantumfusion.dashloader.mixin.MixinPlugin");

	boolean IS_SODIUM_LOAD = isClassFound("me.jellysquid.mods.sodium.mixin.SodiumMixinPlugin");
	boolean IS_JEI_LOAD = isClassFound("mezz.jei.core.search.SearchMode");
	boolean IS_REI_LOAD = isClassFound("me.shedaniel.rei.api.common.entry.EntryStack");
	boolean IS_MEI_LOAD = isClassFound("dev.emi.emi.api.EmiFillAction");
	boolean IS_RUBIDIUM_LOAD = IS_SODIUM_LOAD;

}
