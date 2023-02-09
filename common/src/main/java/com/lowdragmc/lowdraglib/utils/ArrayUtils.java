package com.lowdragmc.lowdraglib.utils;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtils {

    public static <T> T[] fromList(List<T> list) {
        return create(list, (Class<T>) list.get(0).getClass());
    }

    public static <T> T[] create(List<T> list, Class<T> type) {
        T[] array = (T[]) Array.newInstance(type, list.size());
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

}
