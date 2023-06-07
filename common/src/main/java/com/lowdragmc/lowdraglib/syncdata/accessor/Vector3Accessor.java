package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Vector3;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote Vector3Accessor
 */
public class Vector3Accessor extends CustomObjectAccessor<Vector3>{

    public Vector3Accessor() {
        super(Vector3.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Vector3 value) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", value.x);
        tag.putDouble("y", value.y);
        tag.putDouble("z", value.z);
        return NbtTagPayload.of(tag);
    }

    @Override
    public Vector3 deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var tag = (CompoundTag)nbtTagPayload.getPayload();
            return new Vector3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        }
        return null;
    }
}
