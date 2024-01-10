package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Getter;
import lombok.Setter;

public class ManagedRef implements IRef {
    protected final IManagedVar<?> field;
    @Getter
    protected boolean isSyncDirty, isPersistedDirty;
    protected boolean lazy = false;
    protected ManagedKey key;
    @Setter
    protected BooleanConsumer onSyncListener = changed -> {
    };
    @Setter
    protected BooleanConsumer onPersistedListener = changed -> {
    };


    protected ManagedRef(IManagedVar<?> field) {
        this.field = field;
    }

    public static ManagedRef create(IManagedVar<?> field, boolean lazy) {
        if (field instanceof IManagedVar.Int) {
            return new IntRef((IManagedVar.Int) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Long) {
            return new LongRef((IManagedVar.Long) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Float) {
            return new FloatRef((IManagedVar.Float) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Double) {
            return new DoubleRef((IManagedVar.Double) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Boolean) {
            return new BooleanRef((IManagedVar.Boolean) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Byte) {
            return new ByteRef((IManagedVar.Byte) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Short) {
            return new ShortRef((IManagedVar.Short) field).setLazy(lazy);
        } else if (field instanceof IManagedVar.Char) {
            return new CharRef((IManagedVar.Char) field).setLazy(lazy);
        } else if (field instanceof ReadOnlyManagedField) {
            return new ReadOnlyManagedRef((ReadOnlyManagedField) field).setLazy(lazy);
        } else {
            return new SimpleObjectRef(field).setLazy(lazy);
        }
    }

    @Override
    public ManagedKey getKey() {
        return key;
    }

    public IRef setKey(ManagedKey key) {
        this.key = key;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends IManagedVar<?>> T getField() {
        return (T) field;
    }

    @Override
    public void clearSyncDirty() {
        isSyncDirty = false;
        if (key.isDestSync()) {
            onSyncListener.accept(false);
        }
    }

    @Override
    public void clearPersistedDirty() {
        isPersistedDirty = false;
        if (key.isPersist()) {
            onPersistedListener.accept(false);
        }
    }

    @Override
    public void markAsDirty() {
        if (key.isDestSync()) {
            isSyncDirty = true;
            onSyncListener.accept(true);
        }
        if (key.isPersist()) {
            isPersistedDirty = true;
            onPersistedListener.accept(true);
        }
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }

    @Override
    public <T> T readRaw() {
        return this.<IManagedVar<T>>getField().value();
    }

    protected ManagedRef setLazy(boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public void update() {
    }

    static class IntRef extends ManagedRef {
        private int oldValue;

        IntRef(IManagedVar.Int field) {
            super(field);
            oldValue = this.<IManagedVar.Int>getField().intValue();
        }

        @Override
        public void update() {
            int newValue = this.<IManagedVar.Int>getField().intValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class LongRef extends ManagedRef {
        private long oldValue;

        LongRef(IManagedVar.Long field) {
            super(field);
            oldValue = this.<IManagedVar.Long>getField().longValue();
        }

        @Override
        public void update() {
            long newValue = this.<IManagedVar.Long>getField().longValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class FloatRef extends ManagedRef {
        private float oldValue;

        FloatRef(IManagedVar.Float field) {
            super(field);
            oldValue = this.<IManagedVar.Float>getField().floatValue();
        }

        @Override
        public void update() {
            float newValue = this.<IManagedVar.Float>getField().floatValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class DoubleRef extends ManagedRef {
        private double oldValue;

        DoubleRef(IManagedVar.Double field) {
            super(field);
            oldValue = this.<IManagedVar.Double>getField().doubleValue();
        }

        @Override
        public void update() {
            double newValue = this.<IManagedVar.Double>getField().doubleValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class BooleanRef extends ManagedRef {
        private boolean oldValue;

        BooleanRef(IManagedVar.Boolean field) {
            super(field);
            oldValue = this.<IManagedVar.Boolean>getField().booleanValue();
        }

        @Override
        public void update() {
            boolean newValue = this.<IManagedVar.Boolean>getField().booleanValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class ByteRef extends ManagedRef {
        private byte oldValue;

        ByteRef(IManagedVar.Byte field) {
            super(field);
            oldValue = this.<IManagedVar.Byte>getField().byteValue();
        }

        @Override
        public void update() {
            byte newValue = this.<IManagedVar.Byte>getField().byteValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class ShortRef extends ManagedRef {
        private short oldValue;

        ShortRef(IManagedVar.Short field) {
            super(field);
            oldValue = this.<IManagedVar.Short>getField().shortValue();
        }

        @Override
        public void update() {
            short newValue = this.<IManagedVar.Short>getField().shortValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

    static class CharRef extends ManagedRef {
        private char oldValue;

        CharRef(IManagedVar.Char field) {
            super(field);
            oldValue = this.<IManagedVar.Char>getField().charValue();
        }

        @Override
        public void update() {
            char newValue = this.<IManagedVar.Char>getField().charValue();
            if (oldValue != newValue) {
                oldValue = newValue;
                markAsDirty();
            }
        }
    }

}
