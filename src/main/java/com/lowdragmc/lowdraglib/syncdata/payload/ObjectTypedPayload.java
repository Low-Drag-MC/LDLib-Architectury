package com.lowdragmc.lowdraglib.syncdata.payload;

import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public abstract class ObjectTypedPayload<T> implements ITypedPayload<T> {

    protected T payload;

    protected ObjectTypedPayload() {
    }

    public ITypedPayload<T> setPayload(T payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public byte getType() {
        return TypedPayloadRegistries.getId(getClass());
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }


    @Override
    public void writePayload(FriendlyByteBuf buf) {
        var nbt = serializeNBT();
        if (nbt instanceof CompoundTag) {
            buf.writeBoolean(true);
            buf.writeNbt((CompoundTag) nbt);
        } else {
            buf.writeBoolean(false);
            CompoundTag root = new CompoundTag();
            if (nbt != null) {
                root.put("d", nbt);
            }
            buf.writeNbt(root);
        }
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            var nbt = buf.readNbt();
            deserializeNBT(nbt);
        } else {
            var root = buf.readNbt();
            Objects.requireNonNull(root);
            var nbt = root.get("d");
            deserializeNBT(nbt);
        }
    }

}
