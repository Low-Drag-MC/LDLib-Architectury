package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class BlockStateAccessor extends CustomObjectAccessor<BlockState>{

    public BlockStateAccessor() {
        super(BlockState.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(BlockState value) {
        return NbtTagPayload.of(NbtUtils.writeBlockState(value));
    }

    @Override
    public BlockState deserialize(ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            return NbtUtils.readBlockState((CompoundTag)nbtTagPayload.getPayload());
        }
        return null;
    }
}
