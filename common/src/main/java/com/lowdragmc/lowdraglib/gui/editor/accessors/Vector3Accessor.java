package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Vector3Configurator;
import com.lowdragmc.lowdraglib.utils.ReflectionUtils;
import com.lowdragmc.lowdraglib.utils.Vector3;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/5/27
 * @implNote Vector3Accessor
 */
@ConfigAccessor
public class Vector3Accessor extends TypesAccessor<Vector3> {

    public Vector3Accessor() {
        super(Vector3.class);
    }

    @Override
    public Vector3 defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            return new Vector3(field.getAnnotation(DefaultValue.class).numberValue()[0], field.getAnnotation(DefaultValue.class).numberValue()[1], field.getAnnotation(DefaultValue.class).numberValue()[2]);
        }
        return Vector3.ZERO.copy();
    }

    @Override
    public Configurator create(String name, Supplier<Vector3> supplier, Consumer<Vector3> consumer, boolean forceUpdate, Field field) {
        var configurator = new Vector3Configurator(name, supplier, consumer, defaultValue(field, ReflectionUtils.getRawType(field.getGenericType())), forceUpdate);
        if (field.isAnnotationPresent(NumberRange.class)) {
            NumberRange range = field.getAnnotation(NumberRange.class);
            configurator = configurator.setRange(range.range()[0], range.range()[1]).setWheel(range.wheel());
        }
        return configurator;
    }

}
