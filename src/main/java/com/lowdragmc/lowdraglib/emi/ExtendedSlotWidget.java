package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.core.mixins.emi.SlotWidgetAccessor;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.utils.Rect;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
@Accessors(chain = true)
public class ExtendedSlotWidget extends SlotWidget {
    @Getter @Setter
    @Nullable
    public Rect scissorBounds = null;
    @Getter
    @Nullable
    public DraggableScrollableWidgetGroup group = null;

    public ExtendedSlotWidget(EmiIngredient stack, int x, int y) {
        super(stack, x, y);
    }

    public ExtendedSlotWidget setGroup(DraggableScrollableWidgetGroup group) {
        group.getMoveCallbacks().add((xOffset, yOffset) -> {
            ((SlotWidgetAccessor)this).setX(this.x + xOffset);
            ((SlotWidgetAccessor)this).setY(this.y + yOffset);
        });
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (scissorBounds != null) {
            graphics.enableScissor(scissorBounds.left, scissorBounds.up, scissorBounds.right, scissorBounds.down);
            super.render(graphics, mouseX, mouseY, delta);
            graphics.disableScissor();
        } else {
            super.render(graphics, mouseX, mouseY, delta);
        }
    }
}
