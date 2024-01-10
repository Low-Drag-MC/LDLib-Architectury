package com.lowdragmc.lowdraglib.gui.editor.configurator;

import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ValueConfigurator<T> extends Configurator {
    protected boolean forceUpdate;
    @Nullable
    protected T value;
    @Nonnull
    protected T defaultValue;
    @Setter
    protected Consumer<T> onUpdate;
    @Setter
    protected Supplier<T> supplier;

    public ValueConfigurator(String name, Supplier<T> supplier, Consumer<T> onUpdate, @Nonnull T defaultValue, boolean forceUpdate) {
        super(name);
        setClientSideWidget();
        this.supplier = supplier;
        this.onUpdate = onUpdate;
        this.defaultValue = defaultValue;
        this.forceUpdate = forceUpdate;
        this.value = supplier.get();
        this.name = name;
    }

    protected void updateValue() {
        if (onUpdate != null) {
            onUpdate.accept(value);
        }
    }

    protected void onValueUpdate(T newValue) {
        value = newValue;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (forceUpdate) {
            onValueUpdate(supplier.get());
        }
    }

}
