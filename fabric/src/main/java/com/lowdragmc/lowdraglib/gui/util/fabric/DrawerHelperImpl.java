package com.lowdragmc.lowdraglib.gui.util.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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
    public static void drawTooltip(PoseStack poseStack, int mouseX, int mouseY, List<Component> tooltipTexts, ItemStack tooltipStack, TooltipComponent tooltipComponent, Font tooltipFont) {
        Minecraft.getInstance().screen.renderTooltip(poseStack, tooltipTexts, Optional.ofNullable(tooltipComponent), mouseX, mouseY);
    }

    public static ClientTooltipComponent getClientTooltipComponent(TooltipComponent component) {
        var result = TooltipComponentCallback.EVENT.invoker().getComponent(component);
        if (result == null) {
            result = ClientTooltipComponent.create(component);
        }
        return result;
    }

}
