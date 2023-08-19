package com.lowdragmc.lowdraglib.client.model.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ModelFactoryImpl
 */
public class ModelFactoryImpl {
    public static ModelBakery getModeBakery() {
        return Minecraft.getInstance().getModelManager().getModelBakery();
    }

    public static UnbakedModel getLDLibModel(UnbakedModel vanilla) {
        return vanilla;
    }

}
