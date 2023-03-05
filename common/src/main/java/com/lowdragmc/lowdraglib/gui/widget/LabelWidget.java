package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@Configurable(name = "ldlib.gui.editor.register.widget.label", collapse = false)
@RegisterUI(name = "label", group = "widget.basic")
public class LabelWidget extends Widget implements IConfigurableWidget {

    @Setter
    @Nonnull
    protected Supplier<String> textSupplier;

    @Configurable(name = "ldlib.gui.editor.name.text")
    private String lastTextValue = "";

    @Configurable
    @NumberColor
    private int color;

    @Configurable
    private boolean dropShadow;

    public LabelWidget() {
        this(0, 0, "label");
    }

    public LabelWidget(int xPosition, int yPosition, String text) {
        this(xPosition, yPosition, ()->text);
    }

    public LabelWidget(int xPosition, int yPosition, Supplier<String> text) {
        super(new Position(xPosition, yPosition), new Size(10, 10));
        setDropShadow(true);
        setTextColor(-1);
        this.textSupplier = text;
        if (isRemote()) {
            lastTextValue = text.get();
            updateSize();
        }
    }

    @ConfigSetter(field = "lastTextValue")
    public void setText(String text) {
        textSupplier = () -> text;
    }

    public LabelWidget setTextColor(int color) {
        this.color = color;
        return this;
    }

    public LabelWidget setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        if (!isClientSideWidget) {
            this.lastTextValue = textSupplier.get();
        }
        buffer.writeUtf(lastTextValue);
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        this.lastTextValue = buffer.readUtf();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!isClientSideWidget) {
            String latest = textSupplier.get();
            if (!latest.equals(lastTextValue)) {
                this.lastTextValue = latest;
                writeUpdateInfo(-1, buffer -> buffer.writeUtf(this.lastTextValue));
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == -1) {
            this.lastTextValue = buffer.readUtf();
            updateSize();
        } else {
            super.readUpdateInfo(id, buffer);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (isClientSideWidget) {
            String latest = textSupplier.get();
            if (!latest.equals(lastTextValue)) {
                this.lastTextValue = latest;
                updateSize();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void updateSize() {
        Font fontRenderer = Minecraft.getInstance().font;
        setSize(new Size(fontRenderer.width(LocalizationUtils.format(lastTextValue)), fontRenderer.lineHeight));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        String suppliedText = LocalizationUtils.format(lastTextValue);
        String[] split = suppliedText.split("\n");
        Font fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        for (int i = 0; i < split.length; i++) {
            int y = position.y + (i * (fontRenderer.lineHeight + 2));
            if (dropShadow) {
                fontRenderer.drawShadow(poseStack, split[i], position.x, y, color);
            } else {
                fontRenderer.draw(poseStack, split[i], position.x, y, color);
            }
        }
    }

    @Override
    public boolean handleDragging(Object dragging) {
        if (dragging instanceof String string) {
            setText(string);
            return true;
        } else return IConfigurableWidget.super.handleDragging(dragging);
    }

}
