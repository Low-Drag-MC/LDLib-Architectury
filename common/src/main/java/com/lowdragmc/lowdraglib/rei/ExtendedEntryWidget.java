package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@Accessors(chain = true)
public class ExtendedEntryWidget extends EntryWidget {
    @Getter @Setter
    @Nullable
    public Rect scissorBounds = null;
    @Getter
    @Nullable
    public DraggableScrollableWidgetGroup group = null;

    public ExtendedEntryWidget(Point point) {
        super(point);
    }

    public ExtendedEntryWidget(Rectangle bounds) {
        super(bounds);
    }

    public ExtendedEntryWidget setGroup(DraggableScrollableWidgetGroup group) {
        group.getMoveCallbacks().add((xOffset, yOffset) -> this.getBounds().translate(xOffset, yOffset));
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
