package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.StringPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class ComponentAccessor extends CustomObjectAccessor<Component> {

    public ComponentAccessor() {
        super(Component.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Component value, HolderLookup.Provider provider) {
        return StringPayload.of(Component.Serializer.toJson(value, provider));
    }

    @Override
    public Component deserialize(AccessorOp op, ITypedPayload<?> payload, HolderLookup.Provider provider) {
        if (payload instanceof StringPayload stringPayload) {
            var json = stringPayload.getPayload();
            return Component.Serializer.fromJson(json, provider);
        }
        return null;
    }
}
