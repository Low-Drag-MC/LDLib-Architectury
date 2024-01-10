package com.lowdragmc.lowdraglib.syncdata.managed;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class ManagedField<T> implements IManagedVar<T> {
    protected Field field;

    protected Class<T> type;
    protected Object instance;

    public static <T> IManagedVar<T> of(Field field, Object instance) {
        var type = field.getType();
        if (type == int.class) {
            return (IManagedVar<T>) new Int(field, instance);
        }
        if (type == long.class) {
            return (IManagedVar<T>) new Long(field, instance);
        }
        if (type == float.class) {
            return (IManagedVar<T>) new Float(field, instance);
        }
        if (type == double.class) {
            return (IManagedVar<T>) new Double(field, instance);
        }
        if (type == boolean.class) {
            return (IManagedVar<T>) new Boolean(field, instance);
        }
        if (type == byte.class) {
            return (IManagedVar<T>) new Byte(field, instance);
        }
        if (type == char.class) {
            return (IManagedVar<T>) new Char(field, instance);
        }
        if (type == short.class) {
            return (IManagedVar<T>) new Short(field, instance);
        }
        return new ManagedField<>(field, instance);
    }

    protected ManagedField(Field field, Object instance) {
        field.setAccessible(true);
        this.type = (Class<T>) field.getType();
        this.field = field;
        this.instance = instance;
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public T value() {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(T value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static class Int extends ManagedField<Integer> implements IManagedVar.Int {
        private Int(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public int intValue() {
            try {
                return field.getInt(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setInt(int value) {
            try {
                field.setInt(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Long extends ManagedField<java.lang.Long> implements IManagedVar.Long {
        private Long(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public long longValue() {
            try {
                return field.getLong(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setLong(long value) {
            try {
                field.setLong(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Float extends ManagedField<java.lang.Float> implements IManagedVar.Float {
        private Float(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public float floatValue() {
            try {
                return field.getFloat(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setFloat(float value) {
            try {
                field.setFloat(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Byte extends ManagedField<java.lang.Byte> implements IManagedVar.Byte {
        private Byte(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public byte byteValue() {
            try {
                return field.getByte(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setByte(byte value) {
            try {
                field.setByte(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Double extends ManagedField<java.lang.Double> implements IManagedVar.Double {
        private Double(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public double doubleValue() {
            try {
                return field.getDouble(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setDouble(double value) {
            try {
                field.setDouble(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Boolean extends ManagedField<java.lang.Boolean> implements IManagedVar.Boolean {
        private Boolean(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public boolean booleanValue() {
            try {
                return field.getBoolean(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setBoolean(boolean value) {
            try {
                field.setBoolean(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Short extends ManagedField<java.lang.Short> implements IManagedVar.Short {
        private Short(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public short shortValue() {
            try {
                return field.getShort(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setShort(short value) {
            try {
                field.setShort(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Char extends ManagedField<Character> implements IManagedVar.Char {
        private Char(Field field, Object instance) {
            super(field, instance);
        }

        @Override
        public char charValue() {
            try {
                return field.getChar(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setChar(char value) {
            try {
                field.setChar(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
