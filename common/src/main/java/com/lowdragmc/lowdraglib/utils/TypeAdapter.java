package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.gui.editor.runtime.AnnotationDetector;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * Implement this interface to use the inside your class to define type convertions to use inside the graph.
 * <br>
 * Example:
 * @<code>
 *     public class CustomConvertions : ITypeAdapter {
 *         public Vector4 convertFloatToVector(float from){
 *             return new Vector4(from, from, from, from);
 *         }
 *     }
 * </code>
 */
public class TypeAdapter {
    public interface ITypeAdapter {
        void onRegister();
    }

    public record convertion(Class from, Class to) {};
    static Map<convertion, Function> adapters = Collections.synchronizedMap(new HashMap<>());
    static Set<convertion> incompatibleTypes = Collections.synchronizedSet(new HashSet<>());
    static boolean adaptersLoaded = false;

    public static <F, T> void registerAdapter(Class<F> from, Class<T> to, Function<F, T> adapter) {
        adapters.put(new convertion(from, to), adapter);
    }

    public static void registerIncompatibleTypes(Class from, Class to) {
        incompatibleTypes.add(new convertion(from, to));
    }

    public static void loadAllAdapters() {
        adaptersLoaded = true;
        AnnotationDetector.REGISTER_TYPE_ADAPTERS.forEach(instance -> {
            instance.onRegister();
            var clazz = instance.getClass();
            for (var method : clazz.getDeclaredMethods()) {
                // not a static method
                if (!Modifier.isStatic(method.getModifiers())){
                    if (method.getReturnType() != void.class && method.getParameterCount() == 1) {
                        var from = method.getParameterTypes()[0];
                        var to = method.getReturnType();
                        adapters.put(new convertion(from, to), obj -> {
                            try {
                                return method.invoke(instance, obj);
                            } catch (Exception e) {
                                return null;
                            }
                        });
                    }
                }
            }
        });
    }

    public static boolean areIncompatible(Class from, Class to) {
        if (!adaptersLoaded)
            loadAllAdapters();
        return incompatibleTypes.stream().anyMatch(t -> t.from == from && t.to == to);
    }

    public static boolean areAssignable(Class from, Class to) {
        if (!adaptersLoaded)
            loadAllAdapters();
        if (areIncompatible(from, to))
            return false;
        return adapters.containsKey(new convertion(from, to));
    }

    public static Object convert(Object from, Class targetType) {
        if (!adaptersLoaded)
            loadAllAdapters();
        var convertion = new convertion(from.getClass(), targetType);
        if (adapters.containsKey(convertion))
            return adapters.get(convertion).apply(from);
        return null;
    }
}
