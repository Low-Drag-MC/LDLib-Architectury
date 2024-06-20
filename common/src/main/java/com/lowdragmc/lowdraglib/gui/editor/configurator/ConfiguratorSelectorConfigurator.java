package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfiguratorSelectorConfigurator<T> extends ConfiguratorGroup {

    protected BiConsumer<T, ConfiguratorSelectorConfigurator<T>> configuratorBuilder;
    protected List<T> candidates;
    protected Function<T, String> mapping;
    protected Map<String, T> nameMap;
    @Getter
    protected SelectorWidget selector;

    @Setter
    protected int max = 5;
    @Setter
    protected boolean isUp = true;

    protected boolean forceUpdate;
    @Nullable
    protected T value;
    @Nonnull
    protected T defaultValue;
    @Setter
    protected Consumer<T> onUpdate;
    @Setter
    protected Supplier<T> supplier;


    public ConfiguratorSelectorConfigurator(String name, boolean isCollapse,
                                            Supplier<T> supplier,
                                            Consumer<T> onUpdate,
                                            @Nonnull T defaultValue,
                                            boolean forceUpdate,
                                            List<T> candidates,
                                            Function<T, String> mapping,
                                            BiConsumer<T, ConfiguratorSelectorConfigurator<T>> configuratorBuilder) {
        super(name, isCollapse);
        this.supplier = supplier;
        this.onUpdate = onUpdate;
        this.defaultValue = defaultValue;
        this.forceUpdate = forceUpdate;
        this.value = supplier.get();
        this.candidates = candidates;
        this.mapping = mapping;
        this.configuratorBuilder = configuratorBuilder;
        this.nameMap = new HashMap<>();
        for (T candidate : candidates) {
            nameMap.put(mapping.apply(candidate), candidate);
        }
    }

    /**
     * when you update value, you have to call it to notify changes.
     * if necessary you should call {@link #onValueUpdate(T)} to update the value. (e.g. do some widget update in the method)
     */
    protected void updateValue() {
        if (onUpdate != null) {
            onUpdate.accept(value);
        }
    }

    /**
     * it will be called when the value is updated and be detected passively.
     * <br/>
     * you can update widget or do something else in this method.
     * <br/>
     * to update the value, call {@link #updateValue()} as well
     */
    protected void onValueUpdate(T newValue) {
        if (newValue == null) newValue = defaultValue;
        if (newValue.equals(value)) return;
        value = newValue;
        selector.setValue(mapping.apply(newValue));
        removeAllConfigurators();
        if (configuratorBuilder != null) {
            configuratorBuilder.accept(value, this);
        }
        computeLayout();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (forceUpdate) {
            onValueUpdate(supplier.get());
        }
    }

    @Override
    public void init(int width) {
        addWidget(selector = new SelectorWidget(leftWidth + 13, 2, width - leftWidth - 3 - rightWidth - 13, 10, nameMap.keySet().stream().toList(), -1)
                .setOnChanged(s -> {
                    value = nameMap.get(s);
                    updateValue();
                    removeAllConfigurators();
                    if (configuratorBuilder != null) {
                        configuratorBuilder.accept(value, this);
                    }
                    computeLayout();
                })
                .setMaxCount(max)
                .setIsUp(isUp)
                .setButtonBackground(ColorPattern.T_GRAY.rectTexture().setRadius(5))
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setValue(mapping.apply(value))
        );
        if (configuratorBuilder != null) {
            configuratorBuilder.accept(value, this);
        }
        super.init(width);
    }

}
