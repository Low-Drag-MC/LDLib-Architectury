package com.lowdragmc.lowdraglib.side;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

/**
 * @author KilaBash
 * @date 2023/2/11
 * @implNote ForgeEventHooks
 */
public class ForgeEventHooks {
    public static void postPlayerContainerEvent(Player player, AbstractContainerMenu container) {
        NeoForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }

    public static void postBackgroundRenderedEvent(Screen screen, GuiGraphics graphics) {
        RenderSystem.depthMask(true);
        NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(screen, graphics));
        RenderSystem.depthMask(false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void postRenderBackgroundEvent(AbstractContainerScreen<?> guiContainer, GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.depthMask(true);
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(guiContainer, graphics, mouseX, mouseY));
        RenderSystem.depthMask(false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void postRenderForegroundEvent(AbstractContainerScreen<?> guiContainer, GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.depthMask(true);
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(guiContainer, graphics, mouseX, mouseY));
        RenderSystem.depthMask(false);
    }
}
