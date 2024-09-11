package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.client.utils.RenderBufferUtils;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.runtime.AnnotationDetector;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.*;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.gui.widget.FreeGraphView;
import com.lowdragmc.lowdraglib.gui.widget.MenuWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class GraphViewWidget extends WidgetGroup {
    private final BaseGraph graph;

    private final FreeGraphView freeGraphView;
    private final Map<BaseNode, NodeWidget> nodeMap = new HashMap<>();
    @Setter
    private NodePortWidget clickedPort;
    @Setter
    @Nullable
    private BaseGraphProcessor processor;
    @Getter
    private Map<String, List<AnnotationDetector.Wrapper<LDLRegister, ? extends BaseNode>>> nodeGroups = new LinkedHashMap<>();

    public GraphViewWidget(BaseGraph graph, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.graph = graph;
        addWidget(this.freeGraphView = new FreeGraphView(0, 0, width, height) {
            @Override
            @Environment(EnvType.CLIENT)
            protected void drawWidgetsBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                drawGraphLines(graphics, mouseX, mouseY, partialTicks);
                super.drawWidgetsBackground(graphics, mouseX, mouseY, partialTicks);
            }
        });
        this.freeGraphView.setDrawGrid(true);
        this.freeGraphView.setBackground(ColorPattern.BLACK.rectTexture());
        this.processor = new BaseGraphProcessor.Process(graph);
        List<String> supportNodeGroups = new ArrayList<>();
        setupNodeGroups(supportNodeGroups);
        for (String group : supportNodeGroups) {
            for (var wrapper : AnnotationDetector.REGISTER_GP_NODES.values()) {
                if (wrapper.annotation().group().equals(group)) {
                    nodeGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(wrapper);
                }
            }
        }
        loadGraph();
    }

    protected void setupNodeGroups(List<String> supportNodeGroups) {
        supportNodeGroups.add("graph_processor.node.base");
        supportNodeGroups.add("graph_processor.node.conditional");
        supportNodeGroups.add("graph_processor.node.math");
        supportNodeGroups.add("graph_processor.node.utils");
    }

    public void loadGraph() {
        freeGraphView.clearAllWidgets();
        nodeMap.clear();
        for (var node : graph.nodes) {
            var nodeWidget = new NodeWidget(this, node);
            freeGraphView.addWidget(nodeWidget);
            nodeMap.put(node, nodeWidget);
        }
        updateComputeOrder();
    }

    public void addEdge(NodePort inputPort, NodePort outputPort) {
        graph.connect(inputPort, outputPort);
        updateComputeOrder();
    }

    public void removeEdge(NodePort port) {
        var edges = new ArrayList<>(port.getEdges());
        for (var edge : edges) {
            graph.disconnect(edge);
        }
        updateComputeOrder();
    }

    public void updateComputeOrder() {
        graph.updateComputeOrder(BaseGraph.ComputeOrderType.DepthFirst);
        if (processor != null) {
            processor.updateComputeOrder();
        }
    }

    public <T, C> MenuWidget<T, C> openMenu(double posX, double posY, TreeNode<T, C> menuNode) {
        var menu = new MenuWidget<>((int) posX, (int) posY, 14, menuNode)
                .setNodeTexture(MenuWidget.NODE_TEXTURE)
                .setLeafTexture(MenuWidget.LEAF_TEXTURE)
                .setNodeHoverTexture(MenuWidget.NODE_HOVER_TEXTURE);
        waitToAdded(menu.setBackground(MenuWidget.BACKGROUND));
        return menu;
    }

    public void openMenu(double posX, double posY, TreeBuilder.Menu menuBuilder) {
        if (menuBuilder == null) return;
        openMenu(posX, posY, menuBuilder.build())
                .setCrossLinePredicate(TreeBuilder.Menu::isCrossLine)
                .setKeyIconSupplier(TreeBuilder.Menu::getIcon)
                .setKeyNameSupplier(TreeBuilder.Menu::getName)
                .setOnNodeClicked(TreeBuilder.Menu::handle);
    }

    protected TreeBuilder.Menu createMenu(double mouseX, double mouseY) {
        return TreeBuilder.Menu.start()
                .branch("add nodes", m -> nodeGroups.forEach((group, wrappers) -> m.branch(group, n -> {
                    for (var wrapper : wrappers) {
                        n.leaf(wrapper.annotation().name(), () -> {
                            var node = wrapper.creator().get();
                            var position = freeGraphView.getViewPosition(mouseX, mouseY);
                            node.position = new Position((int) position.x, (int) position.y);
                            graph.addNode(node);
                            var nodeWidget = new NodeWidget(this, node);
                            freeGraphView.addWidget(nodeWidget);
                            nodeMap.put(node, nodeWidget);
                            updateComputeOrder();
                        });
                    }
                })));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (processor != null) {
            processor.run();
        }
    }

    @Environment(EnvType.CLIENT)
    private void drawGraphLines(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // existing edges
        for (var edge : graph.edges) {
            var outputPosition = nodeMap.get(edge.outputNode).portMap.get(edge.outputPort).getPortPosition();
            var inputPosition = nodeMap.get(edge.inputNode).portMap.get(edge.inputPort).getPortPosition();
            drawEdge(graphics, outputPosition.vec2(), inputPosition.vec2());
        }
        // dragging edge
        if (clickedPort != null) {
            var outputPosition = clickedPort.isInput ? new Vec2(mouseX, mouseY) : clickedPort.getPortPosition().vec2();
            var inputPosition = clickedPort.isInput ? clickedPort.getPortPosition().vec2() : new Vec2(mouseX, mouseY);
            drawEdge(graphics, outputPosition, inputPosition);
        }
    }

    @Environment(EnvType.CLIENT)
    private void drawEdge(GuiGraphics graphics, Vec2 outputPosition, Vec2 inputPosition) {
        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        var outputController = outputPosition.add(new Vec2(20, 0));
        var inputController = inputPosition.add(new Vec2(-20, 0));
        RenderBufferUtils.drawColorLines(graphics.pose(), buffer, List.of(outputPosition, outputController), ColorPattern.BLUE.color, ColorPattern.BLUE.color, 0.75f);
        RenderBufferUtils.drawColorLines(graphics.pose(), buffer, List.of(outputController, inputController), ColorPattern.BLUE.color, ColorPattern.BLUE.color, 0.75f);
        RenderBufferUtils.drawColorLines(graphics.pose(), buffer, List.of(inputController, inputPosition), ColorPattern.BLUE.color, ColorPattern.BLUE.color, 0.75f);

        tesselator.end();
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var result = super.mouseReleased(mouseX, mouseY, button);
        clickedPort = null;
        if (!result) {
            if (button == 1) {
                openMenu(mouseX, mouseY, createMenu(mouseX, mouseY));
            }
        }
        return result;
    }
}
