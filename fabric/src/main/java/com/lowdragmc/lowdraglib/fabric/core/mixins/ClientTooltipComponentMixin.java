package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.client.utils.WidgetClientTooltipComponent;
import com.lowdragmc.lowdraglib.gui.util.WidgetTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2023/6/29
 * @implNote ClientTooltipComponentMixin
 */
@Mixin(ClientTooltipComponent.class)
public class ClientTooltipComponentMixin {
    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At(value = "RETURN"), cancellable = true)
    private static void injectCreateClientTooltipComponent(TooltipComponent visualTooltipComponent, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (visualTooltipComponent instanceof WidgetTooltipComponent widgetTooltipComponent) {
            cir.setReturnValue(new WidgetClientTooltipComponent(widgetTooltipComponent));
        }
    }
}
