package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import java.lang.ref.WeakReference;

public class ReadonlyRef implements IRef {
    protected final boolean lazy;
    private ManagedKey key;
    private boolean changed;
    protected BooleanConsumer onChanged = changed -> {
    };
    protected final WeakReference<?> reference;

    public ReadonlyRef(boolean lazy, Object value) {
        this.lazy = lazy;
        this.reference = new WeakReference<>(value);
        init();
    }

    protected void init() {
        if (!lazy) {
            if (getReference().get() instanceof IContentChangeAware<?> handler) {
                replaceHandler(handler);
            } else {
                throw new IllegalArgumentException("complex sync field must be an IContentChangeAware if not lazy!");
            }
        }
    }

    protected void replaceHandler(IContentChangeAware<?> handler) {
        var onContentChanged = handler.getOnContentsChanged();
        if (onContentChanged != null) {
            handler.setOnContentsChanged(() -> {
                setChanged(true);
                onContentChanged.run();
            });
        } else {
            handler.setOnContentsChanged(() -> setChanged(true));
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

    public WeakReference<?> getReference() {
        return reference;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean changed) {
        onChanged.accept(changed);
        this.changed = changed;
    }

    @Override
    public void setChangeListener(BooleanConsumer listener) {
        this.onChanged = listener;
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }


    @Override
    public <T> T readRaw() {
        //noinspection unchecked
        return (T) this.reference.get();
    }
}
