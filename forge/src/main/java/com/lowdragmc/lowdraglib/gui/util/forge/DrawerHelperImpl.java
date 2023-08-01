package com.lowdragmc.lowdraglib.gui.util.forge;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * @author KilaBash
 * @date 2023/8/1
 * @implNote DrawerHelperImpl
 */
public class DrawerHelperImpl {
    public static void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY, List<Component> tooltipTexts, ItemStack tooltipStack, TooltipComponent tooltipComponent, Font tooltipFont) {
        graphics.renderTooltip(tooltipFont, tooltipTexts, Optional.ofNullable(tooltipComponent), tooltipStack, mouseX, mouseY);
    }
}
