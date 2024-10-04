package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLib;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public class ReflectionUtils {
    public static Class<?> getRawType(Type type, Class<?> fallback) {
        var rawType = getRawType(type);
        return rawType != null ? rawType : fallback;
    }
    public static Class<?> getRawType(Type type) {
        return switch (type) {
            case Class<?> aClass -> aClass;
            case GenericArrayType genericArrayType -> getRawType(genericArrayType.getGenericComponentType());
            case ParameterizedType parameterizedType -> getRawType(parameterizedType.getRawType());
            case null, default -> null;
        };
    }

    public static <A extends Annotation> void findAnnotationClasses(Class<A> annotationClass, Consumer<Class<?>> consumer, Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotation.annotationData().containsKey("modID") && annotation.annotationData().get("modID") instanceof String modID) {
                    if (!modID.isEmpty() && !ModList.get().isLoaded(modID)) {
                        continue;
                    }
                }
                if (annotationType.equals(annotation.annotationType())) {
                    try {
                        consumer.accept(Class.forName(annotation.memberName(), false, ReflectionUtils.class.getClassLoader()));
                    } catch (Throwable throwable) {
                        LDLib.LOGGER.error("Failed to load class for notation: {}", annotation.memberName(), throwable);
                    }
                }
            }
        }
        onFinished.run();
    }
}
