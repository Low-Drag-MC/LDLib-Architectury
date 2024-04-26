package com.lowdragmc.lowdraglib.syncdata.payload;

import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

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
    public void writePayload(RegistryFriendlyByteBuf buf) {
        var nbt = serializeNBT(buf.registryAccess());
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
    public void readPayload(RegistryFriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            var nbt = buf.readNbt();
            deserializeNBT(nbt, buf.registryAccess());
        } else {
            var root = buf.readNbt();
            Objects.requireNonNull(root);
            var nbt = root.get("d");
            deserializeNBT(nbt, buf.registryAccess());
        }
    }

}
