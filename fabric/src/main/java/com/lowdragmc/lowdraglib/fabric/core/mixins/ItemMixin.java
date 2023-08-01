package com.lowdragmc.lowdraglib.fabric.core.mixins;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.compass.ItemLookupWidget;
import com.lowdragmc.lowdraglib.gui.util.WidgetTooltipComponent;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author KilaBash
 * @date 2023/8/1
 * @implNote ItemMixin
 */
@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "getTooltipImage", at = @At(value = "RETURN"), cancellable = true)
    private void injectTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        var result = cir.getReturnValue();
        if (result.isEmpty()) {
            if (LDLib.isRemote()) {
                long id = Minecraft.getInstance().getWindow().getWindow();
                var isCPressed = InputConstants.isKeyDown(id, GLFW.GLFW_KEY_C);
                if (CompassManager.INSTANCE.hasCompass(stack.getItem())) {
                    if (isCPressed) {
                        CompassManager.INSTANCE.onCPressed(stack);
                        cir.setReturnValue(Optional.of(new WidgetTooltipComponent(new ItemLookupWidget())));
                    } else {
                        cir.setReturnValue(Optional.of(new WidgetTooltipComponent(new LabelWidget(0, 0,"ldlib.compass.c_press").setTextColor(0xff555555).setDropShadow(false))));
                    }
                    return;
                }
                CompassManager.INSTANCE.clearCPressed();
            }
        }
    }
}
