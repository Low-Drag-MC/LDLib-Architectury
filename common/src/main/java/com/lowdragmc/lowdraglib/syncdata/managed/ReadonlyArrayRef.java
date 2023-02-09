package com.lowdragmc.lowdraglib.syncdata.managed;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;

import java.util.Collection;

public class ReadonlyArrayRef extends ReadonlyRef {
    public ReadonlyArrayRef(boolean isLazy, Object value) {
        super(isLazy, value);
    }

    @Override
    protected void init() {
        var value = getReference().get();
        if (value instanceof IContentChangeAware<?> handler) {
            super.init();
            return;
        }
        if (isLazy()) {
            return;
        }
        var type = value.getClass();
        if (type.isArray()) {
            var componentType = type.getComponentType();
            if (!IContentChangeAware.class.isAssignableFrom(componentType)) {
                throw new IllegalArgumentException("complex sync field must be an IContentChangeAware if not lazy!");
            }
            for (var handler : (IContentChangeAware<?>[]) value) {
                replaceHandler(handler);
            }
            return;
        } else if (value instanceof Collection<?> collection) {
            for (var item : collection) {
                if (item instanceof IContentChangeAware<?> handler) {
                    replaceHandler(handler);
                } else {
                    throw new IllegalArgumentException("complex sync field must be an IContentChangeAware if not lazy!");
                }
            }

            return;
        }
        throw new IllegalArgumentException("Field must be an array or collection");
    }


}
