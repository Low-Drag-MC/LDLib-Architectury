package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.model.custommodel.LDLMetadataSection;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author KilaBash
 * @date 2023/3/24
 * @implNote TextureAtlasMixin
 */
@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    //TODO Fix CTM
//    @Inject(method = "getBasicSpriteInfos", at = @At(value = "HEAD"))
//    protected void injectStateToModelLocation(ResourceManager resourceManager, Set<ResourceLocation> spriteNames, CallbackInfoReturnable<Collection<TextureAtlasSprite.Info>> cir) {
//        Set<ResourceLocation> append = new HashSet<>();
//        for (var spriteName : spriteNames) {
//            var data = LDLMetadataSection.getMetadata(LDLMetadataSection.spriteToAbsolute(spriteName));
//            if (data != null && data.connection != null) {
//                append.add(data.connection);
//            }
//        }
//        if (!append.isEmpty()) {
//            spriteNames.addAll(append);
//        }
//    }
}
