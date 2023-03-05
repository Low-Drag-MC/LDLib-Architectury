package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.SyncUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;

public class ManagedArrayLikeRef extends ManagedRef implements IArrayRef {
    private final IntSet dirty = new IntOpenHashSet();
    protected Object oldValue;
    protected int oldLength;
    protected final boolean isArray;

    public ManagedArrayLikeRef(IManagedVar<?> field, boolean lazy) {
        super(field);
        this.lazy = lazy;
        isArray = field.getType().isArray();
        if (!isArray && !Collection.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Field %s is not an array or collection".formatted(field));
        }
        Object value = getField().value();
        if (value != null) {
            var array = SyncUtils.copyArrayLike(value, isArray);
            this.oldValue = array;
            this.oldLength = Array.getLength(array);
        }
    }

    @Override
    public void update() {
        Object newValue = getField().value();
        if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null) || (oldValue != null && checkArrayLikeChanges(oldValue, newValue))) {
            if (newValue != null) {
                var array = SyncUtils.copyArrayLike(newValue, isArray);
                this.oldValue = array;
                this.oldLength = Array.getLength(array);
            } else {
                this.oldValue = null;
                this.oldLength = 0;
            }
            setChanged(true);
        }
    }

    protected boolean checkArrayLikeChanges(@NotNull Object oldValue, @NotNull Object newValue) {
        if (isArray) {
            if(Array.getLength(newValue) != oldLength) {
                setChanged(true);
                dirty.clear();
                return true;
            }
            Object[] a = (Object[]) oldValue;
            Object[] b = (Object[]) newValue;
            for (int i = 0; i < a.length; i++) {
                if (SyncUtils.isChanged(a[i], b[i])) {
                    setChanged(i);
                }
            }
            return isChanged();
        }
        if (newValue instanceof Collection<?> collection) {
            if(collection.size() != oldLength) {
                setChanged(true);
                dirty.clear();
                return true;
            }
            var array = (Object[]) oldValue;
            int i = 0;
            for (var item : collection) {
                var oldItem = array[i];
                if ((oldItem == null && item != null) || (oldItem != null && item == null) || (oldItem != null && SyncUtils.isChanged(oldItem, item))) {
                    setChanged(i);
                }
                i++;
            }
            return isChanged();
        }
        throw new IllegalArgumentException("Value %s is not an array or collection".formatted(newValue));
    }

    @Override
    public void setChanged(boolean changed) {
        super.setChanged(changed);
        if (!changed) {
            dirty.clear();
        }
    }

    @Override
    public void setChanged(int index) {
        setChanged(true);
        dirty.add(index);
    }

    @Override
    public IntSet getChanged() {
        return dirty;
    }
}

