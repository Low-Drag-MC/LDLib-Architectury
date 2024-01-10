package com.lowdragmc.lowdraglib.core.mixins.accessor;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote VertexFormatAccessor
 */
@Mixin(VertexFormat.class)
public interface VertexFormatAccessor {
    @Accessor IntList getOffsets();
}
