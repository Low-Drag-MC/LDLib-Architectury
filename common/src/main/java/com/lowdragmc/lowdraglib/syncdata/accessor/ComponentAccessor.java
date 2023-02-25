package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.StringPayload;
import net.minecraft.network.chat.Component;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote BlockStateAccessor
 */
public class ComponentAccessor extends CustomObjectAccessor<Component>{

    public ComponentAccessor() {
        super(Component.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Component value) {
        return StringPayload.of(Component.Serializer.toJson(value));
    }

    @Override
    public Component deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof StringPayload stringPayload) {
            var json = stringPayload.getPayload();
            return Component.Serializer.fromJson(json);
        }
        return null;
    }
}
