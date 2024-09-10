package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.client.utils.RenderBufferUtils;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseGraph;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.FreeGraphView;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GraphViewWidget extends WidgetGroup {
    private final BaseGraph graph;

    private final FreeGraphView freeGraphView;
    private final Map<BaseNode, NodeWidget> nodeMap = new HashMap<>();

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
        loadGraph();
    }

    public void loadGraph() {
        for (var node : graph.nodes) {
            var nodeWidget = new NodeWidget(this, node);
            freeGraphView.addWidget(nodeWidget);
            nodeMap.put(node, nodeWidget);
        }
    }

    @Environment(EnvType.CLIENT)
    private void drawGraphLines(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (var edge : graph.edges) {
            var outputPosition = nodeMap.get(edge.outputNode).portMap.get(edge.outputPort).getPortPosition();
            var inputPosition = nodeMap.get(edge.inputNode).portMap.get(edge.inputPort).getPortPosition();
            drawEdge(graphics, buffer, outputPosition, inputPosition);
        }

        tesselator.end();
        RenderSystem.defaultBlendFunc();
    }

    @Environment(EnvType.CLIENT)
    private void drawEdge(GuiGraphics graphics, BufferBuilder buffer, Position outputPosition, Position inputPosition) {
        var outputController = outputPosition.add(20, 0).vec2();
        var inputController = inputPosition.add(-20, 0).vec2();
        var center = outputController.add(inputController).scale(0.5f);
        var pintsOut = DrawerHelper.getSmoothPoints(outputPosition.vec2(), outputController, center, 4, 10);
        var pintsIn = DrawerHelper.getSmoothPoints(center, inputController, inputPosition.vec2(), 4, 10);
        var points = new ArrayList<>(pintsOut);
        points.addAll(pintsIn);
        RenderBufferUtils.drawColorLines(graphics.pose(), buffer, points, ColorPattern.BLUE.color, ColorPattern.BLUE.color, 0.75f);
    }
}
