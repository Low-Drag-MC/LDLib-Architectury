package com.lowdragmc.lowdraglib.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public class ClientProxy {

    /**
     * should be called when Minecraft is prepared.
     */
    public static void init() {
        Shaders.init();
        DrawerHelper.init();
        CompassManager.INSTANCE.init();
    }

    @ApiStatus.Internal
    public static final Multimap<ResourceLocation, Material> SCRAPED_TEXTURES = HashMultimap.create();
    @ApiStatus.Internal
    public static final Object2BooleanMap<ResourceLocation> WRAPPED_MODELS = new Object2BooleanLinkedOpenHashMap<>();

    @ApiStatus.Internal
    public static void textureScraped(ResourceLocation modelLocation, Material material) {
        SCRAPED_TEXTURES.put(modelLocation, material);
    }

}
