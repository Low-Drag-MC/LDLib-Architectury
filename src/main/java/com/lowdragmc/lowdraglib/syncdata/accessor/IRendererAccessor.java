package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.ISerializableRenderer;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.nbt.CompoundTag;

public class IRendererAccessor extends CustomObjectAccessor<IRenderer> {

    public IRendererAccessor() {
        super(IRenderer.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, IRenderer value) {
        if (value instanceof ISerializableRenderer serializableRenderer) {
            return NbtTagPayload.of(ISerializableRenderer.serializeWrapper(serializableRenderer));
        }
        return NbtTagPayload.of(new CompoundTag());
    }

    @Override
    public IRenderer deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload && nbtTagPayload.getPayload() instanceof CompoundTag tag) {
            return ISerializableRenderer.deserializeWrapper(tag);
        }
        return IRenderer.EMPTY;
    }

}
