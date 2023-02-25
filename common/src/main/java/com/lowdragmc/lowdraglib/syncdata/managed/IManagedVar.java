package com.lowdragmc.lowdraglib.syncdata.managed;


public interface IManagedVar<T> {
    T value();
    void set(T value);
    boolean isPrimitive();
    Class<T> getType();

    interface Int extends IManagedVar<Integer> {
        void setInt(int value);
        int intValue();

        @Override
        @Deprecated
        default Integer value() {
            return intValue();
        }

        @Override
        @Deprecated
        default void set(Integer value) {
            setInt(value);
        }
    }

    interface Long extends IManagedVar<java.lang.Long> {
        void setLong(long value);
        long longValue();

        @Override
        @Deprecated
        default java.lang.Long value() {
            return longValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Long value) {
            setLong(value);
        }
    }

    interface Float extends IManagedVar<java.lang.Float> {
        void setFloat(float value);
        float floatValue();

        @Override
        @Deprecated
        default java.lang.Float value() {
            return floatValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Float value) {
            setFloat(value);
        }
    }

    interface Double extends IManagedVar<java.lang.Double> {
        void setDouble(double value);
        double doubleValue();

        @Override
        @Deprecated
        default java.lang.Double value() {
            return doubleValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Double value) {
            setDouble(value);
        }
    }

    interface Boolean extends IManagedVar<java.lang.Boolean> {
        void setBoolean(boolean value);
        boolean booleanValue();

        @Override
        @Deprecated
        default java.lang.Boolean value() {
            return booleanValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Boolean value) {
            setBoolean(value);
        }
    }

    interface Byte extends IManagedVar<java.lang.Byte> {
        void setByte(byte value);
        byte byteValue();

        @Override
        @Deprecated
        default java.lang.Byte value() {
            return byteValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Byte value) {
            setByte(value);
        }
    }

    interface Short extends IManagedVar<java.lang.Short> {
        void setShort(short value);
        short shortValue();

        @Override
        @Deprecated
        default java.lang.Short value() {
            return shortValue();
        }

        @Override
        @Deprecated
        default void set(java.lang.Short value) {
            setShort(value);
        }
    }

    interface Char extends IManagedVar<Character> {
        void setChar(char value);
        char charValue();

        @Override
        @Deprecated
        default Character value() {
            return charValue();
        }

        @Override
        @Deprecated
        default void set(Character value) {
            setChar(value);
        }
    }

}
