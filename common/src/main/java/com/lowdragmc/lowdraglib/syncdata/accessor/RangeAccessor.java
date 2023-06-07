package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.Range;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote RangeAccessor
 */
public class RangeAccessor extends CustomObjectAccessor<Range>{

    public RangeAccessor() {
        super(Range.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Range value) {
        CompoundTag tag = new CompoundTag();
        if (value.getA() instanceof Float || value.getA() instanceof Double) {
            tag.putFloat("a", value.getA().floatValue());
        } else {
            tag.putInt("a", value.getA().intValue());
        }
        if (value.getB() instanceof Float || value.getB() instanceof Double) {
            tag.putFloat("b", value.getB().floatValue());
        } else {
            tag.putInt("b", value.getB().intValue());
        }
        return NbtTagPayload.of(tag);
    }

    @Override
    public Range deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload && nbtTagPayload.getPayload() instanceof CompoundTag tag) {
            Number a, b;
            if (tag.contains("a", Tag.TAG_INT)) {
                a = tag.getInt("a");
            } else {
                a = tag.getFloat("a");
            }
            if (tag.contains("b", Tag.TAG_INT)) {
                b = tag.getInt("b");
            } else {
                b = tag.getFloat("b");
            }
            return new Range(a, b);
        }
        return null;
    }
}
