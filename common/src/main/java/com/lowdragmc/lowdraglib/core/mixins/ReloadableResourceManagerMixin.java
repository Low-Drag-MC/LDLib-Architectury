package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.model.custommodel.LDLMetadataSection;
import com.lowdragmc.lowdraglib.gui.texture.ShaderTexture;
import com.lowdragmc.lowdraglib.utils.CustomResourcePack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {

    @ModifyVariable(method = "createReload", at = @At("HEAD"), index = 4, argsOnly = true)
    private List<PackResources> injectCreateReload(List<PackResources> resourcePacks) {
        if (LDLib.isClient()) {
            resourcePacks = lowDragLib$injectClientResourcePack(resourcePacks);
        }
        return resourcePacks;
    }

    @Unique
    @NotNull
    @Environment(EnvType.CLIENT)
    private static List<PackResources> lowDragLib$injectClientResourcePack(List<PackResources> resourcePacks) {
        LDLMetadataSection.clearCache();
        if (LDLib.isRemote()) {
            ShaderTexture.clearCache();
        }
        var mutableList = new ArrayList<>(resourcePacks);
        mutableList.add(new CustomResourcePack(LDLib.getLDLibDir(), LDLib.MOD_ID, PackType.CLIENT_RESOURCES));
        return mutableList;
    }

}
