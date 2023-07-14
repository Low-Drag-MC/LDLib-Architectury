package com.lowdragmc.lowdraglib.side.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidHelper
 */
public class FluidHelper {
    @ExpectPlatform
    public static long getBucket() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getColor(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Environment(EnvType.CLIENT)
    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Component getDisplayName(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getTemperature(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isLighterThanAir(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canBePlacedInWorld(FluidStack fluidStack, BlockAndTintGetter level, BlockPos pos) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean doesVaporize(FluidStack fluidStack, Level level, BlockPos pos) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static SoundEvent getEmptySound(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static SoundEvent getFillSound(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Object toRealFluidStack(FluidStack fluidStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getUnit() {
        throw new AssertionError();
    }
}
