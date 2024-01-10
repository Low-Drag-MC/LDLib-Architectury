package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ITagSerializableAccessor extends ReadonlyAccessor {
    @Override
    public boolean hasPredicate() {
        return true;
    }

    @Override
    public boolean test(Class<?> type) {
        return INBTSerializable.class.isAssignableFrom(type);
    }

    @Override
    public ITypedPayload<?> readFromReadonlyField(AccessorOp op, Object obj) {
        if(!(obj instanceof INBTSerializable<?> serializable)) {
            throw new IllegalArgumentException("Field %s is not INBTSerializable".formatted(obj));
        }

        var nbt = serializable.serializeNBT();

        return new NbtTagPayload().setPayload(nbt);
    }

    @Override
    public void writeToReadonlyField(AccessorOp op, Object obj, ITypedPayload<?> payload) {
        if(!(obj instanceof INBTSerializable<?>)) {
            throw new IllegalArgumentException("Field %s is not INBTSerializable".formatted(obj));
        }

        if(!(payload instanceof NbtTagPayload nbtPayload)) {
            throw new IllegalArgumentException("Payload %s is not NbtTagPayload".formatted(payload));
        }

        //noinspection unchecked
        ((INBTSerializable<Tag>) obj).deserializeNBT(nbtPayload.getPayload());
    }
}
