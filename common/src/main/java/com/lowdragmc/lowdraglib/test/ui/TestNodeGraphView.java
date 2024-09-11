package com.lowdragmc.lowdraglib.test.ui;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseGraph;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.math.AdderNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base.Vector3Node;
import com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.base.NumberNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.widget.GraphViewWidget;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.player.Player;

@LDLRegisterClient(name="node_graph", group = "ui_test")
@NoArgsConstructor
public class TestNodeGraphView implements IUITest {
    @Override
    public ModularUI createUI(IUIHolder holder, Player entityPlayer) {
        var graph = new BaseGraph();
        var x = BaseNode.createFromType(NumberNode.class, new Position(5, 5));
        var z = BaseNode.createFromType(NumberNode.class, new Position(205, 5));
        var vec3 = BaseNode.createFromType(Vector3Node.class, new Position(360, 160));
        var adder = BaseNode.createFromType(AdderNode.class, new Position(75, 100));
        graph.addNode(x);
        graph.addNode(z);
        graph.addNode(vec3);
        graph.addNode(adder);
        return IUITest.super.createUI(holder, entityPlayer)
                .widget(new GraphViewWidget(graph, 0, 0, 500, 500));
    }
}
