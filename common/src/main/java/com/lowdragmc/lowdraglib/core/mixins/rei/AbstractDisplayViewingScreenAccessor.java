package com.lowdragmc.lowdraglib.core.mixins.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.impl.client.gui.screen.AbstractDisplayViewingScreen;
import me.shedaniel.rei.impl.display.DisplaySpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = AbstractDisplayViewingScreen.class, remap = false)
public interface AbstractDisplayViewingScreenAccessor {

    @Accessor
    Map<DisplayCategory<?>, List<DisplaySpec>> getCategoryMap();

}
