package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.NodePort;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NodeWidget extends WidgetGroup {
    private final BaseNode node;
    private final GraphViewWidget graphView;
    protected WidgetGroup title;
    protected WidgetGroup ports;
    protected WidgetGroup content;
    protected final Map<NodePort, NodePortWidget> portMap = new HashMap<>();
    // runtime
    protected boolean isDragging = false;
    protected double lastMouseX, lastMouseY;
    protected Position lastPosition;

    public NodeWidget(GraphViewWidget graphView, BaseNode node) {
        super(node.position, new Size(node.getMinWidth(), 30));
        this.graphView = graphView;
        this.node = node;
        addWidget(title = new WidgetGroup(0, 0, node.getMinWidth(), 15));
        title.setBackground(new ColorRectTexture(ColorPattern.GRAY.color).setTopRadius(5));
        addWidget(ports = new WidgetGroup(0, 15, node.getMinWidth(), 15));
        ports.setDynamicSized(true);
        ports.setLayout(Layout.HORIZONTAL_TOP);
        addWidget(content = new WidgetGroup(0, 30, node.getMinWidth(), 0));
        content.setBackground(new ColorRectTexture(ColorPattern.DARK_GRAY.color));
        reloadWidget();
    }

    public void reloadWidget() {
        var width = node.getMinWidth();
        var height = 15;
        portMap.clear();
        title.clearAllWidgets();
        ports.clearAllWidgets();
        content.clearAllWidgets();

        // load inputs
        var inputGroup = new WidgetGroup(0, 0, 0, 0);
        inputGroup.setBackground(ColorPattern.GRAY.rectTexture());
        var portHeight = Math.max(this.node.getInputPorts().size(), this.node.getOutputPorts().size()) * 15;
        var inputWidth = 0;
        for (var port : this.node.getInputPorts()) {
            var portWidget = new NodePortWidget(this, port, true);
            inputWidth = Math.max(inputWidth, portWidget.getSizeWidth());
            inputGroup.addWidget(portWidget);
            portMap.put(port, portWidget);
        }
        // load outputs
        var outputGroup = new WidgetGroup(0, 0, 0, 0);
        outputGroup.setBackground(ColorPattern.DARK_GRAY.rectTexture());
        var outputWidth = 0;
        for (var port : this.node.getOutputPorts()) {
            var portWidget = new NodePortWidget(this, port, false);
            outputWidth = Math.max(outputWidth, portWidget.getSizeWidth());
            outputGroup.addWidget(portWidget);
            portMap.put(port, portWidget);
        }
        width = Math.max(width, inputWidth + outputWidth);
        var inputPercent = inputWidth * 1f / (inputWidth + outputWidth);
        inputGroup.setSize((int) (inputPercent * width), portHeight);
        outputGroup.setSize(width - (int) (inputPercent * width), portHeight);
        inputGroup.setLayout(Layout.VERTICAL_LEFT);
        outputGroup.setLayout(Layout.VERTICAL_RIGHT);
        ports.addWidget(inputGroup);
        ports.addWidget(outputGroup);

        height += ports.getSizeHeight();

        // title
        title.setSize(width, 15);
        title.addWidget(new ImageWidget(6, 2, width, 11,
                new TextTexture().setSupplier(() -> node.getName() + "(%d)".formatted(node.getComputeOrder())).setWidth(width - 12)
                        .setType(TextTexture.TextType.ROLL)));
        // split line
        title.addWidget(new ImageWidget(0, 14, width, 1, ColorPattern.BLACK.rectTexture()));

        // content
        ConfiguratorGroup group = new ConfiguratorGroup("", false);
        node.buildConfigurator(group);
        int h = 1;
        for (Configurator configurator : group.getConfigurators()) {
            configurator.init(width - 4);
            configurator.computeHeight();
            configurator.setSelfPosition(new Position(2, h));
            h += configurator.getSize().height + 5;
            content.addWidget(configurator);
        }
        if (h > 1) {
            content.addWidget(new ImageWidget(0, 0, width, 1, ColorPattern.BLACK.rectTexture()));
            h -= 5;
        } else {
            h = 0;
        }
        content.setSelfPosition(new Position(0, height));
        content.setSize(width, h);
        height += h;
        setSize(width, height);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        if (isMouseOverElement(mouseX, mouseY)) {
            DrawerHelper.drawFrameRoundBox(graphics, Rect.ofRelative((int) getPositionX(), getSizeWidth(), (int) getPositionY(), getSizeHeight()),
                    1,
                    new Vector4f(5, 0, 5, 0),
                    new Vector4f(5, 0, 5, 0),
                    ColorPattern.BLUE.color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        if (button == 0 && title.isMouseOverElement(mouseX, mouseY)) {
            isDragging = true;
            lastPosition = getSelfPosition();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            setSelfPosition(lastPosition.add((int) (mouseX - lastMouseX), (int) (mouseY - lastMouseY)));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
