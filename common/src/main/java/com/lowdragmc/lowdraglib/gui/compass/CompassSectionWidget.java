package com.lowdragmc.lowdraglib.gui.compass;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author KilaBash
 * @date 2022/8/26
 * @implNote BookTabGroup
 */
public class CompassSectionWidget extends WidgetGroup {

    protected final CompassView compassView;
    protected final CompassSection section;
    protected float xOffset, yOffset;
    protected float scale = 1;
    protected double lastMouseX, lastMouseY;
    protected boolean isDragging = false;

    public CompassSectionWidget(CompassView compassView, CompassSection section) {
        super(0, 0, compassView.getSize().width - CompassView.LIST_WIDTH, compassView.getSize().height);
        this.setBackground(section.getBackgroundTexture().get());
        this.compassView = compassView;
        this.section = section;
        this.resetFitScale();
        addWidget(new ButtonWidget(10, 10, 20, 20,
                new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, Icons.ROTATION),
                cd -> resetFitScale()).setHoverTooltips(Component.translatable("ldlib.gui.compass.reset_view")));
    }

    public void resetFitScale() {
        int minX, minY, maxX, maxY;
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;
        for (CompassNode node : section.nodes.values()) {
            Position position = node.getPosition();
            minX = Math.min(minX, position.x - node.size);
            minY = Math.min(minY, position.y - node.size);
            maxX = Math.max(maxX, position.x + node.size);
            maxY = Math.max(maxY, position.y + node.size);
        }
        this.xOffset = minX;
        this.yOffset = minY;
        var scaleWidth = (float) getSize().width / (maxX - minX);
        var scaleHeight = (float) getSize().height / (maxY - minY);
        this.scale = Math.min(scaleWidth, scaleHeight);
        if (scale < 0.5f) {
            this.scale = 0.5f;
        }
        this.xOffset -= (getSize().width / scale - (maxX - minX)) / 2;
        this.yOffset -= (getSize().height / scale - (maxY - minY)) / 2;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        if (isMouseOverElement(mouseX, mouseY)) {
            isDragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        if (isMouseOverElement(mouseX, mouseY)) {
            int newMouseX = (int) ((mouseX - this.getPosition().x) / scale + xOffset);
            int newMouseY = (int) ((mouseY - this.getPosition().y) / scale + yOffset);
            for (CompassNode node : section.nodes.values()) {
                var nodePosition = node.getPosition();
                if (isMouseOver(nodePosition.x - node.size / 2, nodePosition.y - node.size / 2, node.size, node.size, newMouseX, newMouseY)) {
                    compassView.openNodeContent(node);
                    return true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            xOffset += (float) (lastMouseX - mouseX) / scale;
            yOffset += (float) (lastMouseY - mouseY) / scale;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (isMouseOverElement(mouseX, mouseY)) {
            var newScale = (float) Mth.clamp(scale + wheelDelta * 0.1f, 0.1f, 10f);
            if (newScale != scale) {
                xOffset += (float) (mouseX - this.getPosition().x) / scale - (float) (mouseX - this.getPosition().x) / newScale;
                yOffset += (float) (mouseY - this.getPosition().y) / scale - (float) (mouseY - this.getPosition().y) / newScale;
                scale = newScale;
            }
        }
        return super.mouseWheelMove(mouseX, mouseY, wheelDelta);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOverElement(mouseX, mouseY)) {
            int newMouseX = (int) ((mouseX - this.getPosition().x) / scale + xOffset);
            int newMouseY = (int) ((mouseY - this.getPosition().y) / scale + yOffset);
            for (CompassNode node : section.nodes.values()) {
                var nodePosition = node.getPosition();
                if (isMouseOver(nodePosition.x - node.size / 2, nodePosition.y - node.size / 2, node.size, node.size, newMouseX, newMouseY)) {
                    gui.getModularUIGui().setHoverTooltip(List.of(node.getChatComponent()), ItemStack.EMPTY, null, null);
                }
            }
        }
        super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        drawBackgroundTexture(graphics, mouseX, mouseY);
        var pos = getPosition();
        var size = getSize();
        graphics.enableScissor(pos.x, pos.y, pos.x + size.width, pos.y + size.height);
        graphics.pose().pushPose();
        graphics.pose().translate(this.getPosition().x, this.getPosition().y, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().translate(-xOffset, -yOffset, 0);
        int newMouseX = (int) ((mouseX - this.getPosition().x) / scale + xOffset);
        int newMouseY = (int) ((mouseY - this.getPosition().y) / scale + yOffset);
        // draw lines
        for (CompassNode node : section.nodes.values()) {
            drawChildLines(graphics, node);
        }
        // draw nodes
        for (CompassNode node : section.nodes.values()) {
            drawNode(graphics, newMouseX, newMouseY, node);
        }
        graphics.pose().popPose();
        graphics.disableScissor();
        drawWidgetsBackground(graphics, mouseX, mouseY, partialTicks);
    }

    @Environment(EnvType.CLIENT)
    protected void drawNode(GuiGraphics graphics, int mouseX, int mouseY, CompassNode node) {
        // draw background
        var nodePosition = node.getPosition();
        boolean isHover = isMouseOver(nodePosition.x - node.size / 2, nodePosition.y - node.size / 2, node.size, node.size, mouseX, mouseY);
        var texture = isHover ? compassView.config.getNodeSelectedBackground() : compassView.config.getNodeBackground();
        texture.draw(graphics, mouseX, mouseY, nodePosition.x - node.size / 2f, nodePosition.y - node.size / 2f, node.size, node.size);
        node.getButtonTexture().draw(graphics, mouseX, mouseY, nodePosition.x - node.size * 8f / 24, nodePosition.y - node.size * 8f / 24, node.size * 16 / 24, node.size * 16 / 24);
    }


    @Environment(EnvType.CLIENT)
    protected void drawChildLines(GuiGraphics graphics, CompassNode node) {
        for (var childNode : section.childNodes.getOrDefault(node, new CompassNode[0])) {
            var from = new Vec2(node.getPosition().x, node.getPosition().y);
            var to = new Vec2(childNode.getPosition().x, childNode.getPosition().y);
            DrawerHelper.drawLines(graphics, List.of(from, to), -1, ColorPattern.T_WHITE.color, 1f);
        }
    }

}
