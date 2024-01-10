package com.lowdragmc.lowdraglib.syncdata.accessor;


import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;

import java.util.Objects;

public abstract class PrimitiveAccessor extends ManagedAccessor {
    private final Class<?>[] operandTypes;

    protected PrimitiveAccessor(Class<?> ...operandTypes) {
        this.operandTypes = operandTypes;
    }

    @Override
    public Class<?>[] operandTypes() {
        return operandTypes;
    }

    protected PrimitiveTypedPayload<?> ensurePrimitive(ITypedPayload<?> payload) {
        if(!(payload instanceof PrimitiveTypedPayload<?> primitivePayload)) {
            throw new IllegalArgumentException("Payload %s is not a primitive payload".formatted(payload));
        }
        return primitivePayload;
    }

    protected <T> IManagedVar<T> ensureType(IManagedVar<?> field, Class<T> clazz) {
        if(!clazz.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Field %s is not of type %s".formatted(field, clazz));
        }
        //noinspection unchecked
        return (IManagedVar<T>) field;
    }


    public static class IntAccessor extends PrimitiveAccessor {

        public IntAccessor() {
            super(int.class, Integer.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Int intField) {
                return PrimitiveTypedPayload.ofInt(intField.intValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not an int field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Int intField) {
                intField.setInt(primitivePayload.getAsInt());
            } else {
                ensureType(field, Integer.class).set(primitivePayload.getAsInt());
            }
        }
    }

    public static class LongAccessor extends PrimitiveAccessor {

        public LongAccessor() {
            super(long.class, Long.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Long longField) {
                return PrimitiveTypedPayload.ofLong(longField.longValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a long field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Long longField) {
                longField.setLong(primitivePayload.getAsLong());
            } else {
                ensureType(field, Long.class).set(primitivePayload.getAsLong());
            }
        }
    }

    public static class FloatAccessor extends PrimitiveAccessor {

        public FloatAccessor() {
            super(float.class, Float.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Float floatField) {
                return PrimitiveTypedPayload.ofFloat(floatField.floatValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a float field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Float floatField) {
                floatField.setFloat(primitivePayload.getAsFloat());
            } else {
                ensureType(field, Float.class).set(primitivePayload.getAsFloat());
            }
        }
    }

    public static class DoubleAccessor extends PrimitiveAccessor {

        public DoubleAccessor() {
            super(double.class, Double.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Double doubleField) {
                return PrimitiveTypedPayload.ofDouble(doubleField.doubleValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a double field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Double doubleField) {
                doubleField.setDouble(primitivePayload.getAsDouble());
            } else {
                ensureType(field, Double.class).set(primitivePayload.getAsDouble());
            }
        }
    }

    public static class BooleanAccessor extends PrimitiveAccessor {

        public BooleanAccessor() {
            super(boolean.class, Boolean.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Boolean booleanField) {
                return PrimitiveTypedPayload.ofBoolean(booleanField.booleanValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a boolean field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Boolean booleanField) {
                booleanField.setBoolean(primitivePayload.getAsBoolean());
            } else {
                ensureType(field, Boolean.class).set(primitivePayload.getAsBoolean());
            }
        }
    }

    public static class ByteAccessor extends PrimitiveAccessor {

        public ByteAccessor() {
            super(byte.class, Byte.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Byte byteField) {
                return PrimitiveTypedPayload.ofByte(byteField.byteValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a byte field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Byte byteField) {
                byteField.setByte(primitivePayload.getAsByte());
            } else {
                ensureType(field, Byte.class).set(primitivePayload.getAsByte());
            }
        }
    }

    public static class CharAccessor extends PrimitiveAccessor {

        public CharAccessor() {
            super(char.class, Character.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Char charField) {
                return PrimitiveTypedPayload.ofChar(charField.charValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a char field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Char charField) {
                charField.setChar(primitivePayload.getAsChar());
            } else {
                ensureType(field, Character.class).set(primitivePayload.getAsChar());
            }
        }
    }

    public static class ShortAccessor extends PrimitiveAccessor {

        public ShortAccessor() {
            super(short.class, Short.class);
        }
        public ITypedPayload<?> readManagedField(AccessorOp op, IManagedVar<?> field) {
            if(field instanceof IManagedVar.Short shortField) {
                return PrimitiveTypedPayload.ofShort(shortField.shortValue());
            }
            var result = PrimitiveTypedPayload.tryOfBoxed(field.value());
            return Objects.requireNonNull(result, "Field %s is not a short field".formatted(field));
        }

        public void writeManagedField(AccessorOp op, IManagedVar<?> field, ITypedPayload<?> payload) {
            var primitivePayload = ensurePrimitive(payload);
            if(field instanceof IManagedVar.Short shortField) {
                shortField.setShort(primitivePayload.getAsShort());
            } else {
                ensureType(field, Short.class).set(primitivePayload.getAsShort());
            }
        }
    }
}
