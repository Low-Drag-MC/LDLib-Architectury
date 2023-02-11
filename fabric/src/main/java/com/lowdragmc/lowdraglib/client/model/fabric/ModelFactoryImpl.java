package com.lowdragmc.lowdraglib.client.model.fabric;

import com.google.common.base.Preconditions;
import net.minecraft.client.resources.model.ModelBakery;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ModelFactoryImpl
 */
public class ModelFactoryImpl {
    public static ModelBakery BAKERY = null;

    public static ModelBakery getModeBakery() {
        return Preconditions.checkNotNull(BAKERY, "Attempted to query model bakery before it has been initialized.");
    }
}
