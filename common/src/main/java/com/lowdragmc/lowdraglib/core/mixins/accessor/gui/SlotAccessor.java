package com.lowdragmc.lowdraglib.core.mixins.accessor.gui;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote AbstractContainerScreenMixin
 */
@Mixin(Slot.class)
public interface SlotAccessor {
    @Accessor("x") int getX();
    @Accessor("y") int getY();
    @Accessor("x") void setX(int x);
    @Accessor("y") void setY(int y);
}
