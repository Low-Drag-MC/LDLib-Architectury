package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.utils;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;

@LDLRegister(name = "print", group = "graph_processor.node.utils")
public class PrintNode extends BaseNode {
    @InputPort
    public Object in;

    @Override
    public int getMinWidth() {
        return 150;
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        father.addConfigurators(new WrapperConfigurator("", new ImageWidget(0, 0, 150, 15,
                new TextTexture("").setWidth(150).setType(TextTexture.TextType.ROLL_ALWAYS)
                        .setSupplier(() -> in == null ? "null" : in.toString())))
                .setRemoveTitleBar(true));
    }
}
