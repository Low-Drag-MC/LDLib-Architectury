package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2023/3/16
 * @implNote ComponentTextWidget
 */
@Accessors(fluent = true)
public class ComponentPanelWidget extends Widget {
    protected int maxWidthLimit;
    @Setter
    protected Consumer<List<Component>> textSupplier;
    @Setter
    protected BiConsumer<String, ClickData> clickHandler;
    private List<Component> lastText = new ArrayList<>();
    private List<FormattedCharSequence> cacheLines = Collections.emptyList();

    public ComponentPanelWidget(int x, int y, Consumer<List<Component>> textSupplier) {
        super(x, y, 0, 0);
        this.textSupplier = textSupplier;
    }

    public ComponentPanelWidget(int x, int y) {
        super(x, y, 0, 0);
    }

    public static Component withButton(Component textComponent, String componentData) {
        var style = textComponent.getStyle();
        style = style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "@!" + componentData));
        style = style.withColor(ChatFormatting.YELLOW);
        return textComponent.copy().withStyle(style);
    }

    public static Component withButton(Component textComponent, String componentData, int color) {
        var style = textComponent.getStyle();
        style = style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "@!" + componentData));
        style = style.withColor(color);
        return textComponent.copy().withStyle(style);
    }

    public static Component withHoverTextTranslate(Component textComponent, Component hover) {
        Style style = textComponent.getStyle();
        style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        return textComponent.copy().withStyle(style);
    }

    public ComponentPanelWidget setMaxWidthLimit(int maxWidthLimit) {
        this.maxWidthLimit = maxWidthLimit;
        if (isRemote()) {
            formatDisplayText();
            updateComponentTextSize();
        }
        return this;
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        textSupplier.accept(lastText);
        buffer.writeVarInt(lastText.size());
        for (Component textComponent : lastText) {
            buffer.writeComponent(textComponent);
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        readUpdateInfo(1, buffer);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        if (isClientSideWidget && isRemote()) {
            lastText.clear();
            textSupplier.accept(lastText);
            formatDisplayText();
            updateComponentTextSize();
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        List<Component> textBuffer = new ArrayList<>();
        textSupplier.accept(textBuffer);
        if (!lastText.equals(textBuffer)) {
            this.lastText = textBuffer;
            writeUpdateInfo(1, buffer -> {
                buffer.writeVarInt(lastText.size());
                for (Component textComponent : lastText) {
                    buffer.writeComponent(textComponent);
                }
            });
        }
    }

    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            this.lastText.clear();
            int count = buffer.readVarInt();
            for (int i = 0; i < count; i++) {
                this.lastText.add(buffer.readComponent());
            }
            formatDisplayText();
            updateComponentTextSize();
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            ClickData clickData = ClickData.readFromBuf(buffer);
            String componentData = buffer.readUtf();
            if (clickHandler != null) {
                clickHandler.accept(componentData, clickData);
            }
        } else {
            super.handleClientAction(id, buffer);
        }
    }

    @Environment(EnvType.CLIENT)
    private void updateComponentTextSize() {
        var fontRenderer = Minecraft.getInstance().font;
        int maxStringWidth = 0;
        int totalHeight = 0;
        for (var textComponent : lastText) {
            maxStringWidth = Math.max(maxStringWidth, fontRenderer.width(textComponent.getVisualOrderText()));
            totalHeight += fontRenderer.lineHeight + 2;
        }
        totalHeight -= 2;
        setSize(new Size(maxWidthLimit == 0 ? maxStringWidth : Math.min(maxWidthLimit, maxStringWidth), totalHeight));
    }

    @Environment(EnvType.CLIENT)
    private void formatDisplayText() {
        var fontRenderer = Minecraft.getInstance().font;
        int maxTextWidthResult = maxWidthLimit == 0 ? Integer.MAX_VALUE : maxWidthLimit;
        this.cacheLines = lastText.stream().flatMap(component ->
                ComponentRenderUtils.wrapComponents(component, maxTextWidthResult, fontRenderer).stream())
                .toList();
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    protected Style getStyleUnderMouse(double mouseX, double mouseY) {
        var fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        var selectedLine = (mouseY - position.y) / (fontRenderer.lineHeight + 2);
        if (mouseX >= position.x && selectedLine >= 0 && selectedLine < cacheLines.size()) {
            var cacheLine = cacheLines.get((int) selectedLine);
            var mouseOffset = (int)(mouseX - position.x);
            return fontRenderer.getSplitter().componentStyleAtWidth(cacheLine, mouseOffset);
        }
        return null;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var style = getStyleUnderMouse(mouseX, mouseY);
        if (style != null) {
            if (style.getClickEvent() != null) {
                ClickEvent clickEvent = style.getClickEvent();
                String componentText = clickEvent.getValue();
                if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL && componentText.startsWith("@!")) {
                    String rawText = componentText.substring(2);
                    ClickData clickData = new ClickData();
                    if (clickHandler != null) {
                        clickHandler.accept(rawText, clickData);
                    }
                    writeClientAction(1, buf -> {
                        clickData.writeToBuf(buf);
                        buf.writeUtf(rawText);
                    });
                    playButtonClickSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInForeground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        var style = getStyleUnderMouse(mouseX, mouseY);
        if (style != null) {
            if (style.getHoverEvent() != null) {
                var hoverEvent = style.getHoverEvent();
                var hoverTips = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                if (hoverTips != null) {
                    gui.getModularUIGui().setHoverTooltip(List.of(hoverTips), ItemStack.EMPTY, null, null);
                    return;
                }
            }
        }
        super.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        var fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        for (int i = 0; i < cacheLines.size(); i++) {
            fontRenderer.draw(poseStack, cacheLines.get(i), position.x, position.y + i * (fontRenderer.lineHeight + 2), -1);
        }
    }
}
