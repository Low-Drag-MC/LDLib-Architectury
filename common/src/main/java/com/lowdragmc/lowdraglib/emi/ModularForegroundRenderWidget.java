package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public class ModularForegroundRenderWidget extends Widget {
    public final ModularWrapper<?> modular;

    public ModularForegroundRenderWidget(ModularWrapper<?> modular) {
        this.modular = modular;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(0, 0, modular.getWidget().getSize().width, modular.getWidget().getSize().height);
    }

    @Override
    public void render(PoseStack graphics, int mouseX, int mouseY, float partialTick) {
        graphics.pushPose();
        graphics.translate(-modular.getLeft(), -modular.getTop(), 0);


        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        modular.modularUI.mainGroup.drawInForeground(graphics, mouseX, mouseY, partialTick);
        modular.modularUI.mainGroup.drawOverlay(graphics, mouseX, mouseY, partialTick);

        // do not draw tooltips here, do it from recipe viewer.
        if (modular.isShouldRenderTooltips() && modular.tooltipTexts != null && !modular.tooltipTexts.isEmpty()) {
            graphics.pushPose();
            graphics.translate(0, 0, 240);
            Minecraft.getInstance().screen.renderTooltip(graphics, modular.tooltipTexts, Optional.ofNullable(modular.tooltipComponent), mouseX, mouseY);
            graphics.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        graphics.popPose();
    }
}
