package com.lowdragmc.lowdraglib.syncdata;

public interface IContentChangeAware {
    void setOnContentsChanged(Runnable onContentChanged);
    Runnable getOnContentsChanged();
}
