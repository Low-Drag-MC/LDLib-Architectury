package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.TabButton;
import com.lowdragmc.lowdraglib.gui.widget.TabContainer;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

@Getter
public class StringTabContainer extends TabContainer {
    public final static int TAB_HEIGHT = 16;
    protected Editor editor;

    protected Map<TabButton, TextTexture> tabTextures;
    protected Map<WidgetGroup, Runnable> onSelected;
    protected Map<WidgetGroup, Runnable> onDeselected;
    public BiConsumer<WidgetGroup, WidgetGroup> onChanged;
    private List<WidgetGroup> tabGroups;

    public StringTabContainer(Editor editor) {
        super(0, 0, editor.getSize().width - editor.getConfigPanel().getSize().width, editor.getSize().height);
        this.editor = editor;
        this.tabTextures = new HashMap<>();
        this.onSelected = new HashMap<>();
        this.onDeselected = new HashMap<>();
        this.tabGroups = new ArrayList<>();
        super.setOnChanged(this::onTabChanged);
    }

    public int getTabIndex() {
        if (focus == null) return -1;
        return tabGroups.indexOf(focus);
    }

    public void switchTabIndex(int index) {
        if (tabGroups.size() > index && index >= 0) {
            switchTag(tabGroups.get(index));
        }
    }

    @Override
    public void clearAllWidgets() {
        super.clearAllWidgets();
        this.tabTextures.clear();
        this.onSelected.clear();
        this.onDeselected.clear();
        this.tabGroups.clear();
    }

    public TabContainer setOnChanged(BiConsumer<WidgetGroup, WidgetGroup> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    protected void onTabChanged(WidgetGroup oldGroup, WidgetGroup newGroup) {
        Optional.ofNullable(onDeselected.get(oldGroup)).ifPresent(Runnable::run);
        Optional.ofNullable(onSelected.get(newGroup)).ifPresent(Runnable::run);
        if (onChanged != null) {
            onChanged.accept(oldGroup, newGroup);
        }
    }

    @Override
    public final void addTab(TabButton tabButton, WidgetGroup tabWidget) {
        super.addTab(tabButton, tabWidget);
        tabGroups.add(tabWidget);
    }

    public void addTab(String name, WidgetGroup group, @Nullable Runnable onSelected, @Nullable Runnable onDeselected) {
        var nameTexture = new TextTexture(name).setType(TextTexture.TextType.ROLL);
        var tabButton = new TabButton(0, 0, 60, TAB_HEIGHT - 2)
                .setTexture(
                        new GuiTextureGroup(
                                ColorPattern.T_GRAY.rectTexture(),
                                nameTexture),
                        new GuiTextureGroup(
                                ColorPattern.T_RED.rectTexture(),
                                nameTexture));
        tabTextures.put(tabButton, nameTexture);
        if (onSelected != null) {
            this.onSelected.put(group, onSelected);
        }
        if (onDeselected != null) {
            this.onDeselected.put(group, onDeselected);
        }
        addTab(tabButton, group);
        if (this.focus == group && onSelected != null) {
            onSelected.run();
        }
        calculateTabSize();
    }

    public void addTab(String name, WidgetGroup group, Runnable onSelected) {
        this.addTab(name, group, onSelected, null);
    }

    public void addTab(String name, WidgetGroup group) {
        this.addTab(name, group, null, null);
    }

    protected void calculateTabSize() {
        int tabWidth = (getSize().getWidth() - 1 - tabs.size()) / this.tabs.size();
        int x = 1;
        int y = editor.getMenuPanel().getSize().height + 1;
        for (var tabButton : this.tabs.keySet()) {
            tabButton.setSelfPosition(new Position(x, y));
            tabButton.setSize(new Size(tabWidth, TAB_HEIGHT - 2));
            x += tabWidth + 1;
            Optional.ofNullable(tabTextures.get(tabButton)).ifPresent(texture -> texture.setWidth(tabWidth));
        }
    }
}
