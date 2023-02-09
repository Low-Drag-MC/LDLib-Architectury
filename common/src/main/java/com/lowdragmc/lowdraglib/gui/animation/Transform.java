package com.lowdragmc.lowdraglib.gui.animation;

import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2022/9/8
 * @implNote MoveIn
 */
public class Transform extends Animation {
    protected int xOffset, yOffset;
    protected float scale = 1;

    public Transform setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    public Transform setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void pre(@NotNull PoseStack poseStack) {
        poseStack.pushPose();
        Position position = widget.getPosition();
        Size size = widget.getSize();
        float oX = position.x + size.width / 2f;
        float oY = position.y + size.height / 2f;
        if (isIn()) {
            poseStack.translate(xOffset * (1 - getTime()), yOffset * (1 - getTime()), 0);
        } else {
            poseStack.translate(xOffset * getTime(), yOffset * getTime(), 0);
        }
        poseStack.translate(oX, oY,0);
        if (isIn()) {
            poseStack.scale(scale + (1 - scale) * getTime(), scale + (1 - scale) * getTime(), 1);
        } else {
            poseStack.scale(scale + (1 - scale) * (1- getTime()), scale + (1 - scale) * (1- getTime()), 1);
        }
        poseStack.translate(-oX, -oY,0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void post(@NotNull PoseStack poseStack) {
        poseStack.popPose();
    }
}
