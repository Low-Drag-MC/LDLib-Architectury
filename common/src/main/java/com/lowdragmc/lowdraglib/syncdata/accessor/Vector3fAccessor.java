package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import org.joml.Vector3f;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote Vector3Accessor
 */
public class Vector3fAccessor extends CustomObjectAccessor<Vector3f>{

    public Vector3fAccessor() {
        super(Vector3f.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Vector3f value) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", value.x);
        tag.putFloat("y", value.y);
        tag.putFloat("z", value.z);
        return NbtTagPayload.of(tag);
    }

    @Override
    public Vector3f deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var tag = (CompoundTag)nbtTagPayload.getPayload();
            return new Vector3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
        }
        return null;
    }
}
