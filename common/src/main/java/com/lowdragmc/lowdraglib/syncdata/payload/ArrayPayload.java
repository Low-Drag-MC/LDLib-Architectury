package com.lowdragmc.lowdraglib.syncdata.payload;

import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;

public class ArrayPayload extends ObjectTypedPayload<ITypedPayload<?>[]> {

    public static ArrayPayload of(ITypedPayload<?>[] payload) {
        return (ArrayPayload) new ArrayPayload().setPayload(payload);
    }
    @Override
    public @Nullable Tag serializeNBT() {
        ListTag list = new ListTag();
        for (var payload : getPayload()) {
            CompoundTag compound = new CompoundTag();
            compound.putByte("t", payload.getType());
            var nbt = payload.serializeNBT();
            if (nbt != null) {
                compound.put("p", nbt);
            }
            list.add(compound);
        }
        return list;
    }

    @Override
    public void deserializeNBT(Tag input) {
        if (!(input instanceof ListTag list)) {
            throw new IllegalArgumentException("Tag %s is not ListTag".formatted(input));
        }
        payload = new ITypedPayload[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Tag t = list.get(i);
            if (!(t instanceof CompoundTag compound)) {
                throw new IllegalArgumentException("Tag %s is not CompoundTag".formatted(t));
            }
            byte type = compound.getByte("t");
            Tag tag = compound.get("p");
            payload[i] = TypedPayloadRegistries.create(type);
            payload[i].deserializeNBT(tag);
        }
    }

    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeVarInt(payload.length);
        for (var payload : payload) {
            buf.writeByte(payload.getType());
            payload.writePayload(buf);
        }
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = new ITypedPayload[buf.readVarInt()];
        for (int i = 0; i < payload.length; i++) {
            byte type = buf.readByte();
            payload[i] = TypedPayloadRegistries.create(type);
            payload[i].readPayload(buf);
        }
    }
}
