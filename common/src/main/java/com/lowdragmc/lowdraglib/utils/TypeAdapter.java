package com.lowdragmc.lowdraglib.utils;

/// <summary>
/// Implement this interface to use the inside your class to define type convertions to use inside the graph.
/// Example:
/// <code>
/// public class CustomConvertions : ITypeAdapter
/// {
///     public static Vector4 ConvertFloatToVector(float from) => new Vector4(from, from, from, from);
///     ...
/// }
/// </code>
/// </summary>

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implement this interface to use the inside your class to define type convertions to use inside the graph.
 */
public class TypeAdapter {
    public record convertion(Class from, Class to) {};

    static Map<convertion, Function<Object, Object>> adapters = new HashMap<>();
    static List<convertion> incompatibleTypes = new ArrayList<>();

    public static boolean areIncompatible(Class from, Class to) {
        return incompatibleTypes.stream().anyMatch(t -> t.from == from && t.to == to);
    }

    public static boolean areAssignable(Class from, Class to) {
        if (areIncompatible(from, to))
            return false;

        return adapters.containsKey(new convertion(from, to));
    }

    public static Object convert(Object from, Class targetType) {
        var convertion = new convertion(from.getClass(), targetType);
        if (adapters.containsKey(convertion))
            return adapters.get(convertion).apply(from);
        return null;
    }
}
