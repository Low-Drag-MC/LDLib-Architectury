package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class BuiltinRegistryAccessor<T> extends CustomObjectAccessor<T> {

    Registry<T> registry;

    public BuiltinRegistryAccessor(Class<T> clazz, Registry<T> registry) {
        super(clazz, true);
        this.registry = registry;
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, T value, HolderLookup.Provider provider) {
        return NbtTagPayload.of(StringTag.valueOf(Optional.ofNullable(registry.getKey(value))
                .map(ResourceLocation::toString).orElse("")));
    }

    @Override
    public T deserialize(AccessorOp op, ITypedPayload<?> payload, HolderLookup.Provider provider) {
        if (payload instanceof NbtTagPayload nbtTagPayload) {
            var key = nbtTagPayload.getPayload().getAsString();
            return key.isEmpty() ? null : registry.get(ResourceLocation.parse(key));
        }
        return null;
    }
}
