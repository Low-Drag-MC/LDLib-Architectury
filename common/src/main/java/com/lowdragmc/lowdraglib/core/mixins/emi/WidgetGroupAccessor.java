package com.lowdragmc.lowdraglib.core.mixins.emi;

import dev.emi.emi.api.widget.Widget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote WidgetGroupMixin
 */
@Mixin(targets = "dev.emi.emi.screen.RecipeScreen$WidgetGroup")
public interface WidgetGroupAccessor {
    @Accessor(remap = false)
    List<Widget> getWidgets();
    @Accessor(value = "x", remap = false)
    int getPositionX();

    @Accessor(value = "y", remap = false)
    int getPositionY();
}
