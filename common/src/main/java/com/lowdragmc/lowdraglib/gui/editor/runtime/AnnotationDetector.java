package com.lowdragmc.lowdraglib.gui.editor.runtime;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.accessors.IConfiguratorAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.editor.ui.menu.MenuTab;
import com.lowdragmc.lowdraglib.gui.editor.ui.view.FloatViewWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote AnnotationDetector
 */
@SuppressWarnings("unchecked")
public class AnnotationDetector {

    public record Wrapper<A extends Annotation, T>(A annotation, Class<? extends T> clazz, Supplier<T> creator) { }

    public static final List<IConfiguratorAccessor<?>> CONFIGURATOR_ACCESSORS = scanClasses(ConfigAccessor.class, IConfiguratorAccessor.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::createNoArgsInstance, (a, b) -> 0, l -> {});
    public static final List<Wrapper<LDLRegister, IGuiTexture>> REGISTER_TEXTURES = scanClasses(LDLRegister.class, IGuiTexture.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::toUINoArgsBuilder, AnnotationDetector::UIWrapperSorter, l -> {});
    public static final List<Wrapper<LDLRegister, Resource>> REGISTER_RESOURCES = scanClasses(LDLRegister.class, Resource.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::toUINoArgsBuilder, AnnotationDetector::UIWrapperSorter, l -> {});
    public static final List<Wrapper<LDLRegister, IConfigurableWidget>> REGISTER_WIDGETS = scanClasses(LDLRegister.class, IConfigurableWidget.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::toUINoArgsBuilder, AnnotationDetector::UIWrapperSorter, l -> {});
    public static final List<Wrapper<LDLRegister, FloatViewWidget>> REGISTER_FLOAT_VIEWS = scanClasses(LDLRegister.class, FloatViewWidget.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::toUINoArgsBuilder, AnnotationDetector::UIWrapperSorter, l -> {});
    public static final List<Wrapper<LDLRegister, MenuTab>> REGISTER_MENU_TABS = scanClasses(LDLRegister.class, MenuTab.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::toUINoArgsBuilder, AnnotationDetector::UIWrapperSorter, l -> {});
    public static final List<IProject> REGISTER_PROJECTS = scanClasses(LDLRegister.class, IProject.class, AnnotationDetector::checkNoArgsConstructor, AnnotationDetector::createNoArgsInstance, (a, b) -> 0, l -> {});

    public static void init() {

    }

    public static <A extends Annotation, T, C> List<C> scanClasses(Class<A> annotationClass, Class<T> baseClazz, BiPredicate<A, Class<? extends T>> predicate, Function<Class<? extends T>, C> mapping, Comparator<C> sorter, Consumer<List<C>> onFinished) {
        ArrayList<C> result = new ArrayList<>();
        ReflectionUtils.findAnnotationClasses(annotationClass, clazz -> {
            if (baseClazz.isAssignableFrom(clazz)) {
                try {
                    Class<? extends T> realClass =  (Class<? extends T>) clazz;
                    if (predicate.test(clazz.getAnnotation(annotationClass), realClass)) {
                        result.add(mapping.apply(realClass));
                    }
                } catch (Throwable e) {
                    LDLib.LOGGER.error("failed to scan annotation {} + base class {} while handling class {} ", annotationClass, baseClazz, clazz, e);
                }
            }
        }, () -> {
            result.sort(sorter);
            onFinished.accept(result);
        });
        return result;
    }

    public static <A, T> boolean checkNoArgsConstructor(A annotation, Class<? extends T> clazz) {
        if (annotation instanceof LDLRegister LDLRegister) {
            if (!LDLRegister.modID().isEmpty() && !LDLib.isModLoaded(LDLRegister.modID())) {
                return false;
            }
        }
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static <T> T createNoArgsInstance(Class<? extends T> clazz) {
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Wrapper<LDLRegister, T> toUINoArgsBuilder(Class<? extends T> clazz) {
        return new Wrapper<>(clazz.getAnnotation(LDLRegister.class), clazz, () -> createNoArgsInstance(clazz));
    }

    public static int UIWrapperSorter(Wrapper<LDLRegister, ?> a, Wrapper<LDLRegister, ?> b) {
        return b.annotation.priority() - a.annotation.priority();
    }

}
