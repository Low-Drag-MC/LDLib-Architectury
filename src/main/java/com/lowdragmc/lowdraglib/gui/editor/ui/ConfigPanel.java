package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ConfigPanel
 */
public class ConfigPanel extends WidgetGroup {
    public static final int WIDTH = 252;
    public static class Tab {
        public static List<Tab> TABS = new ArrayList<>();
        public static final Tab WIDGET = registerTab(Icons.WIDGET_SETTING);
        public static final Tab RESOURCE = registerTab(Icons.RESOURCE_SETTING);

        public final ResourceTexture icon;
        public final Consumer<ConfiguratorGroup> configurable;

        private Tab(ResourceTexture icon, Consumer<ConfiguratorGroup> configurable) {
            this.icon = icon;
            this.configurable= configurable;
        }

        public static Tab registerTab(ResourceTexture icon) {
            return registerTab(icon, father -> {});
        }

        public static Tab registerTab(ResourceTexture icon, Consumer<ConfiguratorGroup> configurable) {
            var tab = new Tab(icon, configurable);
            TABS.add(tab);
            return tab;
        }
    }

    @Getter
    protected final Editor editor;
    @Getter
    protected final Map<Tab, IConfigurable> focus = new HashMap<>(Tab.TABS.size());
    protected final Map<Tab, DraggableScrollableWidgetGroup> configuratorGroup = new HashMap<>(Tab.TABS.size());
    protected final Map<Tab, List<Configurator>> configurators = new HashMap<>(Tab.TABS.size());

    protected TabContainer tabContainer;
    @Getter
    protected HsbColorWidget palette;


    public ConfigPanel(Editor editor) {
        super(editor.getSize().getWidth() - WIDTH, 0, WIDTH, editor.getSize().height);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        this.setBackground(ColorPattern.T_BLACK.rectTexture());
        addWidget(new ImageWidget(0, 10, WIDTH, 10, new TextTexture("ldlib.gui.editor.configurator").setWidth(202)));
        addWidget(new ImageWidget(-20, 30, 20, Tab.TABS.size() * 20, ColorPattern.T_BLACK.rectTexture().setLeftRadius(8)));

        addWidget(tabContainer = new TabContainer(0, 0, WIDTH, editor.getSize().height));
        int y = 34;

        for (Tab tab : Tab.TABS) {
            tabContainer.addTab(new TabButton(-16, y, 12, 12).setTexture(
                            tab.icon,
                            tab.icon.copy().setColor(ColorPattern.T_GREEN.color)
                    ),
                    configuratorGroup.computeIfAbsent(tab, key -> new DraggableScrollableWidgetGroup(0, 25, WIDTH, editor.getSize().height - 25)
                            .setYScrollBarWidth(2).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1)))
            );
            configurators.put(tab, new ArrayList<>());
            y += 20;
        }

        super.initWidget();
    }

    public void clearAllConfigurators(Tab tab) {
        this.focus.remove(tab);
        configuratorGroup.get(tab).clearAllWidgets();
        configurators.get(tab).clear();
    }

    public void openConfigurator(Tab tab, IConfigurable configurable) {
        switchTag(tab);
        if (Objects.equals(configurable, this.focus.get(tab))) return;
        clearAllConfigurators(tab);
        this.focus.put(tab, configurable);
        ConfiguratorGroup group = new ConfiguratorGroup("", false);
        tab.configurable.accept(group);
        configurable.buildConfigurator(group);
        for (Configurator configurator : group.getConfigurators()) {
            configurator.setConfigPanel(this, tab);
            configurator.init(WIDTH - 2);
            this.configurators.get(tab).add(configurator);
            configuratorGroup.get(tab).addWidget(configurator);
        }
        computeLayout(tab);
        configuratorGroup.get(tab).setScrollYOffset(0);
    }

    public void switchTag(Tab tab) {
        tabContainer.switchTag(configuratorGroup.get(tab));
    }
    
    public void computeLayout(Tab tab) {
        int height = 0;
        for (Configurator configurator : configurators.get(tab)) {
            configurator.computeHeight();
            configurator.setSelfPosition(new Position(0, height - configuratorGroup.get(tab).getScrollYOffset()));
            height += configurator.getSize().height + 5;
        }
        configuratorGroup.get(tab).computeMax();
    }
}
