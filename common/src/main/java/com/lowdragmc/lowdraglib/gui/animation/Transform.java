package com.lowdragmc.lowdraglib.gui.animation;

import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.IEase;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/9/8
 * @implNote MoveIn
 */
@Accessors(chain = true, fluent = true)
public class Transform extends Animation {
    protected int xOffset, yOffset;
    @Setter
    protected float scale = 1;
    protected boolean in;

    public Transform offset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    public Transform setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public Transform duration(long duration) {
        return (Transform) super.duration(duration);
    }

    @Override
    public Transform delay(long delay) {
        return (Transform) super.delay(delay);
    }

    @Override
    public Transform ease(IEase ease) {
        return (Transform) super.ease(ease);
    }

    @Override
    public Transform onUpdate(FloatConsumer onUpdate) {
        return (Transform) super.onUpdate(onUpdate);
    }

    @Override
    public Animation onFinish(Runnable onFinish) {
        return super.onFinish(onFinish);
    }

    @Override
    public Animation size(@Nullable Size size) {
        return super.size(size);
    }

    @Override
    public Animation position(@Nullable Position position) {
        return super.position(position);
    }

    public boolean isIn() {
        return in;
    }

    public boolean isOut() {
        return !in;
    }

    public Animation setIn() {
        this.in = true;
        return this;
    }

    public Animation setOut() {
        this.in = false;
        return this;
    }

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

    @Environment(EnvType.CLIENT)
    public void post(@NotNull PoseStack poseStack) {
        poseStack.popPose();
    }

    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        float tickTime = getTick();
        if (tickTime >= delay) {
            if (interpolator != null) {
                interpolator.update(tickTime);
            }
            pre(poseStack);
            widget.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
            post(poseStack);
        } else if (isOut()) {
            widget.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        }
    }

    @Environment(EnvType.CLIENT)
    public void drawInForeground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        float tickTime = getTick();
        if (tickTime >= delay) {
            if (interpolator != null) {
                interpolator.update(tickTime);
            }
            pre(poseStack);
            widget.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
            post(poseStack);
        } else if (isOut()) {
            widget.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
        }
    }
}
