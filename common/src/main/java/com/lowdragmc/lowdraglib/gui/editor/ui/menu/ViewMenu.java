package com.lowdragmc.lowdraglib.gui.editor.ui.menu;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.editor.ui.view.FloatViewWidget;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote ViewMenu
 */
@RegisterUI(name = "view", group = "menu", priority = 100)
public class ViewMenu extends MenuTab {
    public final Map<String, FloatViewWidget> openedViews = new HashMap<>();

    protected TreeBuilder.Menu createMenu() {
        var viewMenu = TreeBuilder.Menu.start();
        for (UIDetector.Wrapper<RegisterUI, FloatViewWidget> wrapper : UIDetector.REGISTER_FLOAT_VIEWS) {
            String translateKey = "ldlib.gui.editor.register.%s.%s".formatted(wrapper.annotation().group(), wrapper.annotation().name());
            String name = wrapper.annotation().name();
            if (openedViews.containsKey(name)) {
                viewMenu.leaf(Icons.CHECK, translateKey, () -> {
                    editor.getFloatView().removeWidget(openedViews.get(name));
                    openedViews.remove(name);
                });
            } else {
                viewMenu.leaf(translateKey, () -> {
                    var view = wrapper.creator().get();
                    openedViews.put(name, view);
                    editor.getFloatView().addWidget(view);
                });
            }
        }
        return viewMenu;
    }

    public boolean isViewOpened(String viewName) {
        return openedViews.containsKey(viewName);
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = super.serializeNBT();
        for (FloatViewWidget view : openedViews.values()) {
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }
}
