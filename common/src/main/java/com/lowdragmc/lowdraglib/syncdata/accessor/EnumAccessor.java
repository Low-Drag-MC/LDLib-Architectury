package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import com.lowdragmc.lowdraglib.syncdata.payload.EnumValuePayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.WeakHashMap;

public class EnumAccessor extends ManagedAccessor {

    private static final WeakHashMap<Class<? extends Enum<?>>, Enum<?>[]> enumCache = new WeakHashMap<>();
    private static final WeakHashMap<Class<? extends Enum<?>>, Map<String, Enum<?>>> enumNameCache = new WeakHashMap<>();

    public static <T extends Enum<T>> T getEnum(Class<T> type, String name) {
        var values = enumNameCache.computeIfAbsent(type, t -> {
            var map = new WeakHashMap<String, Enum<?>>();
            for (var value : t.getEnumConstants()) {
                String enumName = getEnumName(value);

                map.put(enumName, value);
            }
            return map;
        });
        var value = values.get(name);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    public static String getEnumName(Enum<?> enumValue) {
        if (enumValue instanceof StringRepresentable provider) {
            return provider.getSerializedName();
        } else {
            return enumValue.name();
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> type, int ordinal) {
        var values = enumCache.computeIfAbsent(type, Class::getEnumConstants);
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException("Invalid ordinal for enum type " + type.getName() + ": " + ordinal);
        }
        return type.cast(values[ordinal]);
    }

    @Override
    public boolean hasPredicate() {
        return true;
    }

    @Override
    public boolean test(Class<?> type) {
        return type.isEnum();
    }


    @Override
    public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
        if (!field.getType().isEnum()) {
            throw new IllegalArgumentException("Field is not an enum");
        }
        var value = field.value();
        if (value != null) {
            var enumVal = (Enum<?>) value;
            var name = getEnumName(enumVal);
            var ordinal = enumVal.ordinal();
            return EnumValuePayload.of(name, ordinal);
        }
        return PrimitiveTypedPayload.ofNull();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
        if (payload instanceof PrimitiveTypedPayload<?> primitive && primitive.isNull()) {
            field.set(null);
            return;
        }
        if (!field.getType().isEnum()) {
            throw new IllegalArgumentException("Field is not an enum");
        }
        if (!(payload instanceof EnumValuePayload enumValue)) {
            throw new IllegalArgumentException("Payload is not an enum value");
        }

        var enumField = (IManagedVar<Enum>) field;

        var ordinal = enumValue.getPayload().ordinal;
        var name = enumValue.getPayload().name;

        Enum value;
        if (ordinal >= 0) {
            value = getEnum((Class<Enum>) field.getType(), ordinal);
        } else {
            value = getEnum((Class<Enum>) field.getType(), name);
        }
        if (value == null) {
            value = getEnum((Class<Enum>) field.getType(), 0);
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid enum value %s (%d) for field %s".formatted(name, ordinal, field));
        }
        enumField.set(value);

    }
}
