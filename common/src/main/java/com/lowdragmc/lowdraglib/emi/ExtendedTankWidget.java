package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.core.mixins.emi.SlotWidgetAccessor;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.TankWidget;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@Accessors(chain = true)
public class ExtendedTankWidget extends TankWidget {
    @Getter
    @Setter
    @Nullable
    public Rect scissorBounds = null;
    @Getter
    @Nullable
    public DraggableScrollableWidgetGroup group = null;

    public ExtendedTankWidget(EmiIngredient stack, int x, int y, int width, int height, long capacity) {
        super(stack, x, y, width, height, capacity);
    }

    public ExtendedTankWidget setGroup(DraggableScrollableWidgetGroup group) {
        group.getMoveCallbacks().add((xOffset, yOffset) -> {
            ((SlotWidgetAccessor)this).setX(this.x + xOffset);
            ((SlotWidgetAccessor)this).setY(this.y + yOffset);
        });
        return this;
    }

    @Override
    public void render(PoseStack graphics, int mouseX, int mouseY, float delta) {
        if (scissorBounds != null) {
            RenderSystem.enableScissor(scissorBounds.left, scissorBounds.up, scissorBounds.right, scissorBounds.down);
            super.render(graphics, mouseX, mouseY, delta);
            RenderSystem.disableScissor();
        } else {
            super.render(graphics, mouseX, mouseY, delta);
        }
    }
}
