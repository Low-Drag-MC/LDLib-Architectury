package com.lowdragmc.lowdraglib.gui.ingredient;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public interface IGhostIngredientTarget {

    @OnlyIn(Dist.CLIENT)
    List<Target> getPhantomTargets(Object ingredient);

}
