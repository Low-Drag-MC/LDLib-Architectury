package com.lowdragmc.lowdraglib.gui.graphprocessor.widget;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
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

@Getter
public class NodePortWidget extends Widget {
    public final NodeWidget widget;
    public final NodePort port;
    public final boolean isInput;

    public NodePortWidget(NodeWidget widget, NodePort port, boolean isInput) {
        super(Position.ORIGIN, Size.ZERO);
        this.widget = widget;
        this.port = port;
        this.isInput = isInput;
        // size is defined by the display name;
        var width = 18;
        if (LDLib.isClient()) {
            width = 18 + Minecraft.getInstance().font.width(port.portData.displayName);
        }
        setSize(width, 15);
        // setup hover tips
        if (port.portData.tooltip != null && !port.portData.tooltip.isEmpty()) {
            setHoverTooltips(port.portData.tooltip.toArray(new String[0]));
        }
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
        var color = isConnecting ? ColorPattern.BLUE.color : ColorPattern.T_BLUE.color;
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
}
