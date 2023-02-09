package com.lowdragmc.lowdraglib.utils;

import java.util.function.Consumer;

public interface ISearch<T> {
    default boolean isManualInterrupt() {return false;}
    void search(String word, Consumer<T> find);
}
