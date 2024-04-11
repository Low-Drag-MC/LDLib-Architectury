package com.lowdragmc.lowdraglib.core.mixins.emi;

import dev.emi.emi.api.widget.SlotWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SlotWidget.class, remap = false)
public interface SlotWidgetAccessor {
    @Accessor(value = "x")
    @Mutable
    void setX(int x);
    @Accessor(value = "y")
    @Mutable
    void setY(int y);
}
