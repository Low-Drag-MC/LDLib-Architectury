package com.lowdragmc.lowdraglib.client.model;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/2/12
 * @implNote IRendererWrapper
 */
@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRendererWrapper {
    IRenderer getRenderer();
    void setRenderer(IRenderer renderer);

}
