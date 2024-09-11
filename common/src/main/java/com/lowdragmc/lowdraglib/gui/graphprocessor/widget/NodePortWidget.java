package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseGraph;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.NodePort;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
public class NodePortWidget extends Widget {
    public final NodeWidget nodeWidget;
    public final NodePort port;
    public final boolean isInput;

    public NodePortWidget(NodeWidget nodeWidget, NodePort port, boolean isInput) {
        super(Position.ORIGIN, Size.ZERO);
        this.nodeWidget = nodeWidget;
        this.port = port;
        this.isInput = isInput;
        // size is defined by the display name;
        var width = 18;
        if (LDLib.isClient()) {
            width = 18 + Minecraft.getInstance().font.width(port.portData.displayName);
        }
        setSize(width, 15);
        // setup hover tips
        var tooltips = new ArrayList<String>();
        var displayType = port.portData.displayType;
        var typeName = displayType.getSimpleName();
        if (displayType == float.class || displayType == int.class || displayType == Float.class || displayType == Integer.class) {
            typeName = "Number";
        }
        tooltips.add("Type: %s".formatted(typeName));
        if (port.portData.tooltip != null && !port.portData.tooltip.isEmpty()) {
            tooltips.addAll(port.portData.tooltip);
        }
        setHoverTooltips(tooltips.toArray(new String[0]));

    }

    public Position getPortPosition() {
        return isInput ? getPosition().add(7, 7) : getPosition().add(getSizeWidth() - 7, 7);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        var isConnecting = !port.getEdges().isEmpty();
        var icon = isConnecting ? Icons.RADIOBOX_MARKED : Icons.RADIOBOX_BLANK;
        var color = ColorPattern.BLUE.color;
        var isHover = isMouseOverElement(mouseX, mouseY);
        var clickedPort = nodeWidget.getGraphView().getClickedPort();
        if (clickedPort == null) {
            color = isHover ? ColorPattern.GREEN.color : color;
        } else if (isHover && clickedPort != this && clickedPort.isInput != isInput){
            var outputPort = clickedPort.isInput ? port : clickedPort.port;
            var inputPort = clickedPort.isInput ? clickedPort.port :port;
            if (BaseGraph.areTypesConnectable(outputPort.portData.displayType, inputPort.portData.displayType)) {
                color = ColorPattern.GREEN.color;
            }
        }
        if (isInput) {
            // draw icon left
            icon.copy().setColor(color).draw(graphics, mouseX, mouseY, getPositionX() + 2, getPositionY() + 2, 11, 11);
            // draw text right
            graphics.drawString(Minecraft.getInstance().font, port.portData.displayName, getPositionX() + 15, getPositionY() + 3, -1);
        } else {
            // draw text left
            graphics.drawString(Minecraft.getInstance().font, port.portData.displayName, getPositionX() + 3, getPositionY() + 3, -1);
            // draw icon right
            icon.copy().setColor(color).draw(graphics, mouseX, mouseY, getPositionX() + getSizeWidth() - 13, getPositionY() + 2, 11, 11);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            if (button == 0 && nodeWidget.getGraphView().getClickedPort() == null) {
                nodeWidget.getGraphView().setClickedPort(this);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var clickedPort = nodeWidget.getGraphView().getClickedPort();
        if (isMouseOverElement(mouseX, mouseY)) {
            if (button == 0 && clickedPort != null && clickedPort != this && clickedPort.isInput != isInput) {
                var outputPort = clickedPort.isInput ? port : clickedPort.port;
                var inputPort = clickedPort.isInput ? clickedPort.port :port;
                if (BaseGraph.areTypesConnectable(outputPort.portData.displayType, inputPort.portData.displayType)) {
                    nodeWidget.getGraphView().addEdge(inputPort, outputPort);
                    // reload the widgets
                    nodeWidget.reloadWidget();
                    clickedPort.getNodeWidget().reloadWidget();
                }
            } else  if (button == 1) {
                var updatedNodes = port.getEdges().stream()
                        .map(port -> port.inputNode == nodeWidget.getNode() ? port.outputNode : port.inputNode)
                        .map(node -> nodeWidget.getGraphView().getNodeMap().get(node)).toList();

                nodeWidget.getGraphView().removeEdge(port);
                nodeWidget.reloadWidget();
                updatedNodes.forEach(NodeWidget::reloadWidget);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
