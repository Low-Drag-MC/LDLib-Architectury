package com.lowdragmc.creategreg.api;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/1/30
 * @implNote ITierProvider
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITierProvider {

    Tier getTier();
}
