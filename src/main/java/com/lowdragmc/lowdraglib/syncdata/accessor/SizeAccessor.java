package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class SizeAccessor extends CustomObjectAccessor<Size>{

    public SizeAccessor() {
        super(Size.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Size value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("width", value.width);
        tag.putInt("height", value.height);
        return NbtTagPayload.of(tag);
    }

    @Override
    public Size deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var tag = (CompoundTag)nbtTagPayload.getPayload();
            return new Size(tag.getInt("width"), tag.getInt("height"));
        }
        return null;
    }
}
