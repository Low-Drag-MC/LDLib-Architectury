package com.lowdragmc.lowdraglib.core.mixins.emi;

import com.lowdragmc.lowdraglib.emi.ModularSlotWidget;
import com.lowdragmc.lowdraglib.emi.ModularWrapperWidget;
import dev.emi.emi.api.recipe.EmiRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote WidgetGroupMixin
 */
@Mixin(targets = "dev.emi.emi.screen.RecipeScreen$WidgetGroup")
public abstract class WidgetGroupMixin {

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(EmiRecipe recipe, List widgets, int x, int y, int width, int height, CallbackInfo ci) {
        for (var widget : widgets) {
            if (widget instanceof ModularWrapperWidget wrapperWidget) {
                wrapperWidget.modular.setEmiRecipeWidget(x, y);
            }
            if (widget instanceof ModularSlotWidget slotWidget) {
                slotWidget.setLayout(x, y);
            }
        }
    }
}
