package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.SyncUtils;

import java.lang.reflect.Array;
import java.util.Collection;

public class ManagedArrayLikeRef extends ManagedRef {
    private Object oldValue;
    private int oldLength;
    private final boolean isArray;

    public ManagedArrayLikeRef(IManagedVar<?> field, boolean lazy) {
        super(field);
        this.lazy = lazy;
        isArray = field.getType().isArray();
        if (!isArray && !Collection.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Field %s is not an array or collection".formatted(field));
        }
    }

    @Override
    public void update() {
        Object newValue = getField().value();
        if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null) || (oldValue != null && SyncUtils.isArrayLikeChanged(oldValue, newValue, oldLength, isArray))) {
            var array = SyncUtils.copyArrayLike(newValue, isArray);
            this.oldValue = array;
            this.oldLength = Array.getLength(array);
            setChanged(true);
        }

    }
}

