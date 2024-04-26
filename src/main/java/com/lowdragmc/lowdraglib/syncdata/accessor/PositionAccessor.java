package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class PositionAccessor extends CustomObjectAccessor<Position>{

    public PositionAccessor() {
        super(Position.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Position value, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", value.x);
        tag.putInt("y", value.y);
        return NbtTagPayload.of(tag);
    }

    @Override
    public Position deserialize(AccessorOp op, ITypedPayload<?> payload, HolderLookup.Provider provider) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var tag = (CompoundTag)nbtTagPayload.getPayload();
            return new Position(tag.getInt("x"), tag.getInt("y"));
        }
        return null;
    }
}
