package com.lowdragmc.lowdraglib.side.fluid.fabric;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
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
public class FluidHelperImpl {

    public static FluidVariant toFluidVariant(FluidStack fluidStack) {
        return FluidVariant.of(fluidStack.getFluid(), fluidStack.getTag());
    }

    public static FluidStack toFluidStack(StorageView<FluidVariant> view) {
        if (view instanceof FluidStack fluidStack) {
            return fluidStack;
        } else {
            return FluidStack.create(view.getResource().getFluid(), view.getAmount(), view.getResource().getNbt());
        }
    }

    public static long getBucket() {
        return FluidConstants.BUCKET;
    }

    public static int getColor(FluidStack fluidStack) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluid())
                .getFluidColor(null, null, fluidStack.getFluid().defaultFluidState());
    }

    @Environment(EnvType.CLIENT)
    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluid())
                .getFluidSprites(null, null, fluidStack.getFluid().defaultFluidState())[0];
    }

    public static Component getDisplayName(FluidStack fluidStack) {
        return FluidVariantAttributes.getName(toFluidVariant(fluidStack));
    }

    public static int getTemperature(FluidStack fluidStack) {
        return FluidVariantAttributes.getTemperature(toFluidVariant(fluidStack));
    }

    public static boolean isLighterThanAir(FluidStack fluidStack) {
        return FluidVariantAttributes.isLighterThanAir(toFluidVariant(fluidStack));
    }

    public static boolean canBePlacedInWorld(FluidStack fluidStack, BlockAndTintGetter level, BlockPos pos) {
        return fluidStack.getFluid().defaultFluidState().createLegacyBlock().isAir();
    }

    public static boolean doesVaporize(FluidStack fluidStack, Level level, BlockPos pos) {
        return false;
    }

    public static SoundEvent getEmptySound(FluidStack fluidStack) {
        return FluidVariantAttributes.getEmptySound(toFluidVariant(fluidStack));
    }

    public static SoundEvent getFillSound(FluidStack fluidStack) {
        return FluidVariantAttributes.getFillSound(toFluidVariant(fluidStack));
    }


}
