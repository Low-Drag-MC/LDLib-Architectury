package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

@LDLRegister(name = "button", group = "widget.basic")
public class ButtonWidget extends Widget implements IConfigurableWidget {

    protected Consumer<ClickData> onPressCallback;

    public ButtonWidget() {
        this(0, 0, 40, 20, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("Button")), null);
    }

    @Override
    public void initTemplate() {
        setHoverBorderTexture(1, -1);
    }

    public ButtonWidget(int xPosition, int yPosition, int width, int height, IGuiTexture buttonTexture, Consumer<ClickData> onPressed) {
        super(xPosition, yPosition, width, height);
        this.onPressCallback = onPressed;
        setBackground(buttonTexture);
    }

    public ButtonWidget(int xPosition, int yPosition, int width, int height, Consumer<ClickData> onPressed) {
        super(xPosition, yPosition, width, height);
        this.onPressCallback = onPressed;
    }

    public ButtonWidget setOnPressCallback(Consumer<ClickData> onPressCallback) {
        this.onPressCallback = onPressCallback;
        return this;
    }

    public ButtonWidget setButtonTexture(IGuiTexture... buttonTexture) {
        super.setBackground(buttonTexture);
        return this;
    }

    public ButtonWidget setHoverTexture(IGuiTexture... hoverTexture) {
        super.setHoverTexture(hoverTexture);
        return this;
    }

    public ButtonWidget setHoverBorderTexture(int border, int color) {
        super.setHoverTexture(new ColorBorderTexture(border, color));
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            ClickData clickData = new ClickData();
            writeClientAction(1, clickData::writeToBuf);
            if (onPressCallback != null) {
                onPressCallback.accept(clickData);
            }
            playButtonClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            ClickData clickData = ClickData.readFromBuf(buffer);
            if (onPressCallback != null) {
                onPressCallback.accept(clickData);
            }
        }
    }
}
