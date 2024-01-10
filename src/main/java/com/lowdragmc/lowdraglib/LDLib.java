package com.lowdragmc.lowdraglib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.json.factory.FluidStackTypeAdapter;
import com.lowdragmc.lowdraglib.json.IGuiTextureTypeAdapter;
import com.lowdragmc.lowdraglib.json.ItemStackTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Mod(LDLib.MOD_ID)
public class LDLib {
    public static final String MOD_ID = "ldlib";
    public static final String NAME = "LowDragLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final String MODID_JEI = "jei";
    public static final String MODID_RUBIDIUM = "rubidium";
    public static final String MODID_REI = "roughlyenoughitems";
    public static final String MODID_EMI = "emi";
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(IGuiTextureTypeAdapter.INSTANCE)
            .registerTypeAdapterFactory(FluidStackTypeAdapter.INSTANCE)
            .registerTypeAdapter(ItemStack.class, ItemStackTypeAdapter.INSTANCE)
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    @Deprecated
    public static File location;

    public LDLib(IEventBus eventBus) {
        LDLib.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ClientProxy(eventBus);
        } else {
            new CommonProxy(eventBus);
        }
    }

    public static void init() {
        LOGGER.info("{} is initializing on platform: {}", NAME, Platform.platformName());
        getLDLibDir();
    }

    public static File getLDLibDir() {
        if (location == null) {
            location = new File(Platform.getGamePath().toFile(), "assets/ldlib");
            if (location.mkdir()) {
                LOGGER.info("create ldlib config folder");
            }
        }
        return location;
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static boolean isClient() {
        return Platform.isClient();
    }

    public static boolean isRemote() {
        if (isClient()) {
            return Minecraft.getInstance().isSameThread();
        }
        return false;
    }

    public static boolean isModLoaded(String mod) {
        return Platform.isModLoaded(mod);
    }

    public static boolean isJeiLoaded() {
        return !isEmiLoaded() && !isReiLoaded() && isModLoaded(MODID_JEI);
    }

    public static boolean isReiLoaded() {
        return isModLoaded(MODID_REI);
    }

    public static boolean isEmiLoaded() {
        return isModLoaded(MODID_EMI);
    }
}
