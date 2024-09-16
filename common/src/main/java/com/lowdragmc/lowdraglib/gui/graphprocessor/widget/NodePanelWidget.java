package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class NodePanelWidget extends DraggablePanelWidget implements SearchComponentWidget.IWidgetSearch<String> {
    private final GraphViewWidget graphView;
    // runtime
    private DraggableScrollableWidgetGroup nodeListView;
    private SearchComponentWidget<String> searchComponent;
    private final Map<String, SelectableWidgetGroup> mapping = new HashMap<>();
    @Nullable
    private String selectedNode;
    private boolean firstClick;
    private String firstClickName;
    private long firstClickTime;

    public NodePanelWidget(GraphViewWidget graphView, int x, int y, int width, int height) {
        super("graph_processor.node_panel", x, y, width, height);
        this.graphView = graphView;
    }

    @Override
    protected void loadWidgets() {
        super.loadWidgets();
        mapping.clear();
        // node list
        nodeListView = new DraggableScrollableWidgetGroup(3, 12, content.getSizeWidth() - 6, content.getSizeHeight() - 15);
        nodeListView.setYScrollBarWidth(4).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(2));
        nodeListView.setLayout(Layout.VERTICAL_LEFT);
        for (var group : graphView.getNodeGroups().entrySet()) {
            // group name
            nodeListView.addWidget(new ImageWidget(0, 0, nodeListView.getSizeWidth(), 10, new TextTexture(group.getKey())));
            // split line
            nodeListView.addWidget(new ImageWidget(0, 0, nodeListView.getSizeWidth() - 4, 1, ColorPattern.WHITE.rectTexture()));
            // nodes
            for (var node : group.getValue()) {
                var selectableWidgetGroup = new SelectableWidgetGroup(0, 0, nodeListView.getSizeWidth(), 10);
                selectableWidgetGroup.setDraggingProvider(() -> node.creator().get(), (n, pos) -> new TextTexture(n.getDisplayName()));
                selectableWidgetGroup.addWidget(new ImageWidget(0, 0, nodeListView.getSizeWidth(), 10,
                        new TextTexture(node.annotation().name()).setWidth(nodeListView.getSizeWidth()).setType(TextTexture.TextType.LEFT_ROLL)));
                selectableWidgetGroup.setOnSelected(s -> selectedNode = node.annotation().name());
                selectableWidgetGroup.setOnUnSelected(s -> selectedNode = null);
                selectableWidgetGroup.setSelectedTexture(ColorPattern.T_GRAY.rectTexture());
                nodeListView.addWidget(selectableWidgetGroup);
                mapping.put(node.annotation().name(), selectableWidgetGroup);
            }
        }
        nodeListView.setLayout(Layout.NONE);
        nodeListView.computeMax();
        content.addWidget(nodeListView);

        // search bar
        content.addWidget(new ImageWidget(0, 2, content.getSizeWidth() - 3, 10, ColorPattern.T_GRAY.rectTexture().setRadius(5)));
        content.addWidget(searchComponent = new SearchComponentWidget<>(3, 2, content.getSizeWidth() - 3, 10, this));
        searchComponent.setShowUp(false);
        searchComponent.setCapacity(5);
        var textFieldWidget = searchComponent.textFieldWidget;
        textFieldWidget.setClientSideWidget();
        textFieldWidget.setCurrentString("");
        textFieldWidget.setBordered(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var result = super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 && nodeListView.isMouseOverElement(mouseX, mouseY)) {
            if (selectedNode != null && mapping.get(selectedNode).isMouseOverElement(mouseX, mouseY)) {
                if (firstClick && firstClickName.equals(selectedNode) && gui.getTickCount() - firstClickTime < 10) {
                    graphView.getNodeGroups().values().stream().flatMap(List::stream)
                            .filter(node -> node.annotation().name().equals(selectedNode))
                            .findFirst()
                            .ifPresent(wrapper -> graphView.addNodeToCenter(wrapper.creator().get()));
                    selectedNode = null;
                    return true;
                }
                firstClick = true;
                firstClickName = selectedNode;
                firstClickTime = gui.getTickCount();
            }
        }
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isKeyDown(GLFW.GLFW_KEY_ENTER)) {
            if (selectedNode != null) {
                graphView.getNodeGroups().values().stream().flatMap(List::stream)
                        .filter(node -> node.annotation().name().equals(selectedNode))
                        .findFirst()
                        .ifPresent(wrapper -> graphView.addNodeToCenter(wrapper.creator().get()));
                selectedNode = null;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public String resultDisplay(String value) {
        return value;
    }

    @Override
    public void selectResult(String value) {
        selectedNode = value;
        nodeListView.setSelected(mapping.get(value));
        graphView.getNodeGroups().values().stream().flatMap(List::stream)
                .filter(node -> node.annotation().name().equals(selectedNode))
                .findFirst()
                .ifPresent(wrapper -> graphView.addNodeToCenter(wrapper.creator().get()));
    }

    @Override
    public void search(String word, Consumer<String> find) {
        var wordLower = word.toLowerCase();
        for (var group : graphView.getNodeGroups().values()) {
            for (var node : group) {
                if (node.annotation().name().toLowerCase().contains(wordLower)) {
                    find.accept(node.annotation().name());
                }
            }
        }
    }
}
