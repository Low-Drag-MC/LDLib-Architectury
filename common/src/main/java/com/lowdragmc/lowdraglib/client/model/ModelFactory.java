package com.lowdragmc.lowdraglib.client.model;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Quaternion;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Author: KilaBash
 * Date: 2022/04/24
 * Description:
 */
@Environment(EnvType.CLIENT)
public class ModelFactory {

    @ExpectPlatform
    public static ModelBakery getModeBakery() {
        throw new AssertionError();
    }

    public static UnbakedModel getUnBakedModel(ResourceLocation modelLocation) {
        return getModeBakery().getModel(modelLocation);
    }

    public static Quaternion getQuaternion(Direction facing) {
        return switch (facing) {
            case UP -> new Quaternion(Mth.HALF_PI, 0, 0, false);
            case DOWN -> new Quaternion(-Mth.HALF_PI, 0, 0, false);
            case EAST -> new Quaternion(0, -Mth.HALF_PI, 0, false);
            case WEST -> new Quaternion(0, Mth.HALF_PI, 0, false);
            case SOUTH -> new Quaternion(0, Mth.PI, 0, false);
            case NORTH -> Quaternion.ONE;
        };
    }

    public static BlockModelRotation getRotation(Direction facing) {
        return switch (facing) {
            case DOWN -> BlockModelRotation.X90_Y0;
            case UP -> BlockModelRotation.X270_Y0;
            case NORTH -> BlockModelRotation.X0_Y0;
            case SOUTH -> BlockModelRotation.X0_Y180;
            case WEST -> BlockModelRotation.X0_Y270;
            case EAST -> BlockModelRotation.X0_Y90;
        };
    }

    public static Either<Material, String> parseTextureLocationOrReference(ResourceLocation pLocation, String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new Material(pLocation, resourcelocation));
            }
        }
    }

    public static Either<Material, String> parseBlockTextureLocationOrReference(String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, resourcelocation));
            }
        }
    }

    private static boolean isTextureReference(String pStr) {
        return pStr.charAt(0) == '#';
    }
}
