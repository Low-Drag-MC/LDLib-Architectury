package com.lowdragmc.lowdraglib.forge.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ModelFactoryImpl
 */
public class ModelFactoryImpl {
    public static ModelBakery getModeBakery() {
        return Minecraft.getInstance().getModelManager().getModelBakery();
    }
}
