package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.editor.ui.menu.MenuTab;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote MenuPanel
 */
public class MenuPanel extends WidgetGroup {
    public static final int HEIGHT = 16;

    @Getter
    protected final Editor editor;
    @Getter
    protected final Map<String, MenuTab> tabs = new LinkedHashMap<>();

    public MenuPanel(Editor editor) {
        super(0, 0, editor.getSize().getWidth() - ConfigPanel.WIDTH, HEIGHT);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        this.setBackground(ColorPattern.T_RED.rectTexture());
        this.addWidget(new ImageWidget(2, 2, 12, 12, new ResourceTexture()));
        if (isRemote()) {
            int x = 20;
            var tag = new CompoundTag();
            try {
                tag = NbtIo.read(new File(editor.getWorkSpace(), "settings/menu.cfg"));
                if (tag == null) {
                    tag = new CompoundTag();
                }
            } catch (IOException e) {
                LDLib.LOGGER.error(e.getMessage());
            }
            for (UIDetector.Wrapper<RegisterUI, MenuTab> wrapper : UIDetector.REGISTER_MENU_TABS) {
                var tab = wrapper.creator().get();
                tabs.put(wrapper.annotation().name(), tab);
                var button = tab.createTabWidget();
                button.addSelfPosition(x, 0);
                x += button.getSize().getWidth();
                addWidget(button);
                if (tag.contains(tab.name())) {
                    tab.deserializeNBT(tag.getCompound(tab.name()));
                }
            }
        }
        super.initWidget();
    }

    public void saveMenuData() {
        var tag = new CompoundTag();
        for (MenuTab tab : tabs.values()) {
            var nbt = tab.serializeNBT();
            if (!nbt.isEmpty()) {
                tag.put(tab.name(), nbt);
            }
        }
        try {
            NbtIo.write(tag, new File(editor.getWorkSpace(), "settings/menu.cfg"));
        } catch (IOException e) {
            LDLib.LOGGER.error(e.getMessage());
        }
    }

}
