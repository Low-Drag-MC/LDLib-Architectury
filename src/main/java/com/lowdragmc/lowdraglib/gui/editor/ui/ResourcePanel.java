package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.animation.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Resources;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ResourcePanel
 */
public class ResourcePanel extends WidgetGroup {
    public static final int HEIGHT = 100;

    @Getter
    protected Editor editor;
    protected ButtonWidget buttonHide;
    protected TabContainer tabContainer;

    @Getter
    @Nullable
    protected Resources resources;

    @Getter
    protected boolean isShow = true;

    public ResourcePanel(Editor editor) {
        super(0, editor.getSize().height - HEIGHT, editor.getSize().getWidth() - ConfigPanel.WIDTH, HEIGHT);
        setClientSideWidget();
        this.editor = editor;
    }

    private void dispose() {
        if (resources != null) {
            resources.dispose();
        }
    }

    @Override
    public void setGui(ModularUI gui) {
        super.setGui(gui);
        if (gui == null) {
            dispose();
        } else {
            getGui().registerCloseListener(this::dispose);
        }
    }

    @Override
    public void initWidget() {
        Size size = getSize();
        this.setBackground(ColorPattern.BLACK.rectTexture());
        addWidget(buttonHide = new ButtonWidget((getSize().width - 30) / 2, -10, 30, 10, new GuiTextureGroup(
                ColorPattern.BLACK.rectTexture(),
                ColorPattern.T_GRAY.borderTexture(1),
                Icons.DOWN
        ), cd -> {
            if (isShow()) {
                hide();
            } else {
                show();
            }
        }).setHoverBorderTexture(1, -1));
        addWidget(new LabelWidget(3, 3, "ldlib.gui.editor.group.resources"));
        addWidget(tabContainer = new TabContainer(0, 15, size.width, size.height - 14));
        tabContainer.setBackground(ColorPattern.T_GRAY.borderTexture(-1));
        super.initWidget();
    }

    public void hide() {
        if (isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .offset(0, HEIGHT)
                    .ease(Eases.EaseQuadOut)
                    .duration(500)
                    .onFinish(() -> {
                        addSelfPosition(0, HEIGHT);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.UP);
                    }));
        }
    }

    public void show() {
        if (!isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .offset(0, -HEIGHT)
                    .ease(Eases.EaseQuadOut)
                    .duration(500)
                    .onFinish(() -> {
                        addSelfPosition(0, -HEIGHT);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.DOWN);
                    }));
        }
    }

    public void loadResource(Resources resources, boolean merge) {
        tabContainer.clearAllWidgets();

        if (!merge && this.resources != null) {
            this.resources.dispose();
        }

        if (!merge || this.resources == null) {
            this.resources = resources;
            resources.load();
        } else {
            this.resources.merge(resources);
        }

        int offset = Minecraft.getInstance().font.width(LocalizationUtils.format("ldlib.gui.editor.group.resources")) + 8;
        for (Resource<?> resource : this.resources.resources.values()) {
            tabContainer.addTab(
                    new TabButton(offset, -15, Minecraft.getInstance().font.width(LocalizationUtils.format(resource.name())) + 8, 15).setTexture(
                            new TextTexture(resource.name()),
                            new GuiTextureGroup(new TextTexture(resource.name(), ColorPattern.T_GREEN.color), ColorPattern.T_GRAY.rectTexture())
                    ),
                    resource.createContainer(this)
            );
            offset += 52;
        }
    }

}
