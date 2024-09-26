package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote WrapperConfigurator
 */
@Accessors(chain = true)
public class WrapperConfigurator extends Configurator {
    public final Widget inner;
    @Setter
    public boolean removeTitleBar;

    public WrapperConfigurator(Widget widget) {
        this("", widget, true);
    }

    public WrapperConfigurator(String name, Widget widget) {
        this(name, widget, false);
    }

    public WrapperConfigurator(String name, Widget widget, boolean removeTitleBar) {
        super(name);
        this.inner = widget;
        this.removeTitleBar = removeTitleBar;
    }

    @Override
    public void computeHeight() {
        super.computeHeight();
        if (removeTitleBar) {
            setSize(new Size(getSize().width, inner.getSize().height + 4));
        } else {
            setSize(new Size(getSize().width, inner.getSize().height + 19));
        }
    }

    @Override
    public void init(int width) {
        super.init(width);
        Size size = inner.getSize();
        inner.setSelfPosition(new Position((width - size.width) / 2, removeTitleBar ? 2 : 17));
        addWidget(inner);
    }
}
