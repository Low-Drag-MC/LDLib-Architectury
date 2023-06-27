package com.lowdragmc.lowdraglib.fabric.core.mixins.emi;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote MinecraftClientMixin
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public Screen screen;

    @Inject(method = "setScreen", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;removed()V", shift = At.Shift.AFTER))
    private void onScreenRemove(@Nullable Screen screen, CallbackInfo ci) {
        clearScreens();
    }

    @Inject(method = "stop", at = @At(value = "RETURN"))
    private void onScreenRemoveBecauseStopping(CallbackInfo ci) {
        clearScreens();
    }

    private void clearScreens() {
        if (this.screen != null) {
            if (LDLib.isEmiLoaded()) {
                if (this.screen instanceof RecipeScreen && !ModularEmiRecipe.CACHE_OPENED.isEmpty()) {
                    synchronized (ModularEmiRecipe.CACHE_OPENED) {
                        ModularEmiRecipe.CACHE_OPENED.forEach(modular -> modular.modularUI.triggerCloseListeners());
                        ModularEmiRecipe.CACHE_OPENED.clear();
                    }
                }
            }
        }
    }
}
