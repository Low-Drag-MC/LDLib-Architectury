package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.google.common.base.Strings;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.BitSet;

public class IManagedAccessor extends ReadonlyAccessor {

    @Override
    public boolean hasPredicate() {
        return true;
    }

    @Override
    public boolean test(Class<?> type) {
        return IManaged.class.isAssignableFrom(type);
    }

    @Override
    public ITypedPayload<?> readFromReadonlyField(AccessorOp op, Object obj) {
        if(!(obj instanceof IManaged managed)) {
            throw new IllegalArgumentException("Field %s is not INBTSerializable".formatted(obj));
        }

        CompoundTag tag;
        if (op == AccessorOp.SYNCED || op == AccessorOp.FORCE_SYNCED) {
            tag = readSyncedFields(managed, new CompoundTag(), op == AccessorOp.FORCE_SYNCED);
        } else {
            tag = readManagedFields(managed, new CompoundTag());
        }
        return new NbtTagPayload().setPayload(tag);
    }

    public static CompoundTag readSyncedFields(IManaged managed, CompoundTag tag, boolean force) {
        BitSet changed = new BitSet();
        var syncedFields = managed.getSyncStorage().getSyncFields();
        var list = new ListTag();
        for (int i = 0; i < syncedFields.length; i++) {
            var field = syncedFields[i];
            if (force || field.isSyncDirty()) {
                changed.set(i);
                var key = field.getKey();

                var payload = key.readSyncedField(field, force);
                CompoundTag payloadTag = new CompoundTag();
                payloadTag.putByte("t", payload.getType());
                var data = payload.serializeNBT();
                if (data != null) {
                    payloadTag.put("d", data);
                }
                list.add(payloadTag);

                field.clearSyncDirty();
            }
        }
        tag.putByteArray("c", changed.toByteArray());
        tag.put("l", list);
        return tag;
    }

    public static CompoundTag readManagedFields(IManaged managed, CompoundTag tag) {
        var persistedFields = managed.getSyncStorage().getPersistedFields();
        for (var persistedField : persistedFields) {
            var fieldKey = persistedField.getKey();

            String key = fieldKey.getPersistentKey();
            if (Strings.isNullOrEmpty(key)) {
                key = fieldKey.getName();
            }

            var nbt = fieldKey.readPersistedField(persistedField);

            if (nbt != null) {
                TagUtils.setTagExtended(tag, key, nbt);
            }
        }
        return tag;
    }

    @Override
    public void writeToReadonlyField(AccessorOp op, Object obj, ITypedPayload<?> payload) {
        if(!(obj instanceof IManaged managed)) {
            throw new IllegalArgumentException("Field %s is not INBTSerializable".formatted(obj));
        }


        if(!(payload instanceof NbtTagPayload nbtPayload && nbtPayload.getPayload() instanceof CompoundTag tag)) {
            throw new IllegalArgumentException("Payload %s is not NbtTagPayload".formatted(payload));
        }

        if(op == AccessorOp.SYNCED || op == AccessorOp.FORCE_SYNCED) {
            var storage = managed.getSyncStorage();
            var syncedFields = storage.getSyncFields();

            var changed = BitSet.valueOf(tag.getByteArray("c"));
            var list = tag.getList("l", 10);
            var payloads = new ITypedPayload<?>[list.size()];

            for (int i = 0; i < payloads.length; i++) {
                CompoundTag payloadTag = list.getCompound(i);
                byte id = payloadTag.getByte("t");
                var p = TypedPayloadRegistries.create(id);
                p.deserializeNBT(payloadTag.get("d"));
                payloads[i] = p;
            }

            writeSyncedFields(storage, syncedFields, changed, payloads);
        } else if (op == AccessorOp.PERSISTED) {
            var refs = managed.getSyncStorage().getPersistedFields();
            writePersistedFields(tag, refs);
        } else {
            throw new IllegalArgumentException("Payload %s does not match op %s".formatted(payload, op));
        }
    }

    public static void writePersistedFields(CompoundTag tag, IRef[] refs) {
        for (var ref : refs) {
            var fieldKey = ref.getKey();
            String key = fieldKey.getPersistentKey();
            if (Strings.isNullOrEmpty(key)) {
                key = fieldKey.getName();
            }

            var nbt = TagUtils.getTagExtended(tag, key);
            if (nbt != null) {
                fieldKey.writePersistedField(ref, nbt);
            }
        }
    }

    public static void writeSyncedFields(IManagedStorage storage, IRef[] syncedFields, BitSet changed, ITypedPayload<?>[] payloads) {
        int j = 0;
        for (int i = 0; i < changed.length(); i++) {
            if (changed.get(i)) {
                var field = syncedFields[i];
                var key = field.getKey();

                boolean hasListener = storage.hasSyncListener(key);
                Object oldValue = null;
                if (hasListener) {
                    oldValue = field.readRaw();
                }
                key.writeSyncedField(field, payloads[j]);
                if(hasListener) {
                    storage.notifyFieldUpdate(key, field.readRaw(), oldValue);
                }

                j++;
            }
        }
    }

}
