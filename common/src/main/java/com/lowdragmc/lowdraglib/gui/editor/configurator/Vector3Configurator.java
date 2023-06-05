package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.utils.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/5/27
 * @implNote Vector3Configurator
 */
public class Vector3Configurator extends ValueConfigurator<Vector3>{

    protected Number min, max, wheel;

    public Vector3Configurator(String name, Supplier<Vector3> supplier, Consumer<Vector3> onUpdate, @NotNull Vector3 defaultValue, boolean forceUpdate) {
        super(name, supplier, onUpdate, defaultValue, forceUpdate);
        if (value == null) {
            value = defaultValue;
        }
        setWheel(.1f);
    }

    public Vector3Configurator setRange(Number min, Number max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public Vector3Configurator setWheel(Number wheel) {
        if (wheel.doubleValue() == 0) return this;
        this.wheel = wheel;
        return this;
    }

    @Override
    protected void onValueUpdate(Vector3 newValue) {
        if (newValue == null) newValue = defaultValue;
        if (newValue.equals(value)) return;
        super.onValueUpdate(newValue);
    }

    @Override
    public void init(int width) {
        super.init(width);
        assert value != null;
        var w = (width - leftWidth - rightWidth) / 3;
        var x = new NumberConfigurator("x", () -> this.value.x, number -> {
            this.value.x = number.doubleValue();
            updateValue();
        }, defaultValue.x, forceUpdate);
        x.setRange(min, max);
        x.setWheel(wheel);
        x.setConfigPanel(configPanel, tab);
        x.init(w);
        x.addSelfPosition(leftWidth, 0);
        addWidget(x);

        var y = new NumberConfigurator("y", () -> this.value.y, number -> {
            this.value.y = number.doubleValue();
            updateValue();
        }, defaultValue.y, forceUpdate);
        y.setRange(min, max);
        y.setWheel(wheel);
        y.setConfigPanel(configPanel, tab);
        y.init(w);
        y.addSelfPosition(leftWidth + w, 0);
        addWidget(y);

        var z = new NumberConfigurator("z", () -> this.value.z, number -> {
            this.value.z = number.doubleValue();
            updateValue();
        }, defaultValue.z, forceUpdate);
        z.setRange(min, max);
        z.setWheel(wheel);
        z.setConfigPanel(configPanel, tab);
        z.init(w);
        z.addSelfPosition(leftWidth + w * 2, 0);
        addWidget(z);
    }
}
