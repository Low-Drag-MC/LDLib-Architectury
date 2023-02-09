package com.lowdragmc.lowdraglib.gui.widget.custom;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

/**
 * @author KilaBash
 * @date 2022/12/12
 * @implNote PlayerInventoryWidget
 */
@RegisterUI(name = "player_inventory", group = "widget.custom")
public class PlayerInventoryWidget extends WidgetGroup {

    public PlayerInventoryWidget() {
        super(0, 0, 172, 86);
        setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                var id = "player_inv_" + (col + (row + 1) * 9);
                var pos = new Position(5 + col * 18, 5 + row * 18);
                var slot = new SlotWidget();
                slot.setSelfPosition(pos);
                slot.setId(id);
                addWidget(slot);
            }
        }
        for (int col = 0; col < 9; col++) {
            String id = "player_inv_" + col;
            var pos = new Position(5 + col * 18, 5 + 58);
            var slot = new SlotWidget();
            slot.setSelfPosition(pos);
            slot.setId(id);
            addWidget(slot);
        }
    }
}
