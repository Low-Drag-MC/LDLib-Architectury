package com.lowdragmc.lowdraglib.syncdata;

public interface IContentChangeAware<T extends IContentChangeAware<T>> {
    T setOnContentsChanged(Runnable onContentChanged);
    Runnable getOnContentsChanged();
}
