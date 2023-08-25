package com.lowdragmc.lowdraglib.side.fluid.forge;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidHelper
 */
public class FluidHelperImpl {

    public static net.minecraftforge.fluids.FluidStack toFluidStack(FluidStack fluidStack) {
        return new net.minecraftforge.fluids.FluidStack(fluidStack.getFluid(), (int) Math.min(fluidStack.getAmount(), Integer.MAX_VALUE), fluidStack.getTag());
    }

    public static FluidStack toFluidStack(net.minecraftforge.fluids.FluidStack fluidStack) {
        return FluidStack.create(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
    }

    public static long getBucket() {
        return FluidType.BUCKET_VOLUME;
    }

    public static int getColor(FluidStack fluidStack) {
        return IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(toFluidStack(fluidStack));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        var texture = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(toFluidStack(fluidStack));
        return texture == null ? null : Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    public static Component getDisplayName(FluidStack fluidStack) {
        return fluidStack.getFluid().getFluidType().getDescription(toFluidStack(fluidStack));
    }

    public static int getTemperature(FluidStack fluidStack) {
        return fluidStack.getFluid().getFluidType().getTemperature(toFluidStack(fluidStack));
    }

    public static boolean isLighterThanAir(FluidStack fluidStack) {
        return fluidStack.getFluid().getFluidType().isLighterThanAir();
    }

    public static boolean canBePlacedInWorld(FluidStack fluidStack, BlockAndTintGetter level, BlockPos pos) {
        return fluidStack.getFluid().getFluidType().canBePlacedInLevel(level, pos, toFluidStack(fluidStack));
    }

    public static boolean doesVaporize(FluidStack fluidStack, Level level, BlockPos pos) {
        return fluidStack.getFluid().getFluidType().isVaporizedOnPlacement(level, pos, toFluidStack(fluidStack));
    }

    public static SoundEvent getEmptySound(FluidStack fluidStack) {
        return fluidStack.getFluid().getFluidType().getSound(toFluidStack(fluidStack), SoundActions.BUCKET_EMPTY);
    }

    public static SoundEvent getFillSound(FluidStack fluidStack) {
        return fluidStack.getFluid().getFluidType().getSound(toFluidStack(fluidStack), SoundActions.BUCKET_FILL);
    }

    public static Object toRealFluidStack(FluidStack fluidStack) {
        return toFluidStack(fluidStack);
    }

    public static String getUnit() {
        return "mB";
    }
}
