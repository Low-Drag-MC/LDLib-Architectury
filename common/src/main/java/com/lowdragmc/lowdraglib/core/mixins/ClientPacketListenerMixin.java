package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleUpdateTags", at = @At("RETURN"))
    private void injectCreateReload(ClientboundUpdateTagsPacket packet, CallbackInfo ci) {
        CompassManager.INSTANCE.onResourceManagerReload(this.minecraft.getResourceManager());
    }

}
