package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.*;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ConfigAccessor
public class FluidStackAccessor extends TypesAccessor<FluidStack> {

    public FluidStackAccessor() {
        super(FluidStack.class);
    }

    @Override
    public FluidStack defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            var annotation = field.getAnnotation(DefaultValue.class);
            return FluidStack.create(BuiltInRegistries.FLUID.get(new ResourceLocation(annotation.stringValue()[0])), (long) annotation.numberValue()[0]);

        }
        return FluidStack.empty();
    }

    @Override
    public Configurator create(String name, Supplier<FluidStack> supplier, Consumer<FluidStack> consumer, boolean forceUpdate, Field field) {
        ConfiguratorGroup group = new ConfiguratorGroup(name);
        var fluidStorage = new FluidStorage(supplier.get());
        var tank = new TankWidget(fluidStorage, 0, 0, 18, 18, false, false);
        tank.setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"));
        tank.setClientSideWidget();
        Consumer<FluidStack> updateStack = stack -> {
            consumer.accept(stack);
            fluidStorage.setFluidInTank(0, stack);
        };
        group.addConfigurators(new FluidConfigurator("id",
                () -> supplier.get().getFluid(),
                item -> {
                    var last = supplier.get();
                    var tag = last.getTag();
                    var count = last.getAmount();
                    var newStack = FluidStack.create(item, Math.max(count, 1));
                    newStack.setTag(tag);
                    updateStack.accept(newStack);
                }, Fluids.EMPTY, forceUpdate));
        var min = 1;
        var max = 64;
        if (field.isAnnotationPresent(NumberRange.class)) {
            min = (int) field.getAnnotation(NumberRange.class).range()[0];
            max = (int) field.getAnnotation(NumberRange.class).range()[1];
        }
        group.addConfigurators(new NumberConfigurator("ldlib.gui.editor.configurator.amount",
                () -> supplier.get().getAmount(),
                count -> updateStack.accept(supplier.get().copy(count.intValue())), 1, forceUpdate)
                .setRange(min, max));
        group.addConfigurators(new CompoundTagAccessor().create("ldlib.gui.editor.configurator.nbt",
                () -> supplier.get().hasTag() ? supplier.get().getTag() : new CompoundTag(),
                tag -> {
                    var last = supplier.get();
                    var fluid = last.getFluid();
                    var count = last.getAmount();
                    var newStack = FluidStack.create(fluid, Math.max(count, 1));
                    if (tag.isEmpty()) {
                        newStack.setTag(null);
                    } else {
                        newStack.setTag(tag);
                    }
                    updateStack.accept(newStack);
                }, false, field));
        group.addConfigurators(new WrapperConfigurator("ldlib.gui.editor.group.preview", tank));
        return group;
    }

}
