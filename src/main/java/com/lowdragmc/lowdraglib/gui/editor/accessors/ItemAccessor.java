package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.configurator.*;
import com.lowdragmc.lowdraglib.utils.ReflectionUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ConfigAccessor
public class ItemAccessor extends TypesAccessor<Item> {

    public ItemAccessor() {
        super(Item.class);
    }

    @Override
    public Item defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            var annotation = field.getAnnotation(DefaultValue.class);
            if (annotation.stringValue().length > 0) {
                return BuiltInRegistries.ITEM.get(new ResourceLocation(annotation.stringValue()[0])).asItem();
            }
        }
        return Items.AIR;
    }

    @Override
    public Configurator create(String name, Supplier<Item> supplier, Consumer<Item> consumer, boolean forceUpdate, Field field) {
        return new ItemConfigurator(name, supplier, consumer, defaultValue(field, ReflectionUtils.getRawType(field.getGenericType())), forceUpdate);
    }

}
