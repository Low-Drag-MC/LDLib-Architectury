package com.lowdragmc.lowdraglib.gui.ingredient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

public interface IGhostIngredientTarget {

    @Environment(EnvType.CLIENT)
    List<Target> getPhantomTargets(Object ingredient);

}
