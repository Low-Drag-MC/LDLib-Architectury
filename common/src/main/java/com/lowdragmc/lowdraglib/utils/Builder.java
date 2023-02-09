package com.lowdragmc.lowdraglib.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/10/22
 * @implNote Builder
 */
public abstract class Builder<T, B extends Builder<T, B>> {
    protected List<String[]> shape = new ArrayList<>();
    protected Map<Character, T> symbolMap = new HashMap<>();

    public B aisle(String... data) {
        this.shape.add(data);
        return (B) this;
    }

    public B where(char symbol, T value) {
        this.symbolMap.put(symbol, value);
        return (B) this;
    }

    public T[][][] bakeArray(Class<T> clazz, T defaultValue) {
        T[][][] Ts = (T[][][]) Array.newInstance(clazz, shape.get(0)[0].length(), shape.get(0).length, shape.size());
        for (int z = 0; z < Ts.length; z++) { //z
            String[] aisleEntry = shape.get(z);
            for (int y = 0; y < shape.get(0).length; y++) {
                String columnEntry = aisleEntry[y];
                for (int x = 0; x < columnEntry.length(); x++) {
                    T info = symbolMap.getOrDefault(columnEntry.charAt(x), defaultValue);
                    Ts[x][y][z] = info;
                }
            }
        }
        return Ts;
    }

    public B shallowCopy() {
        Builder builder;
        try {
            builder = this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        builder.shape = new ArrayList<>(this.shape);
        builder.symbolMap = new HashMap<>(this.symbolMap);
        return (B) builder;
    }

}
