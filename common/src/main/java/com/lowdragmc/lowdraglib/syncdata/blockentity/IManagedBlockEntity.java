package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.syncdata.IManaged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface IManagedBlockEntity extends IManaged {

    /**
     * @return the block entity type
     */
    default BlockEntityType<?> getBlockEntityType() {
        return getSelf().getType();
    }

    /**
     * Get the position of this block entity, used to identify it.
     */
    default BlockPos getCurrentPos() {
        return getSelf().getBlockPos();
    }

    /**
     * @return the BlockEntity itself
     */
    default BlockEntity getSelf() {
        return (BlockEntity) this;
    }

}
