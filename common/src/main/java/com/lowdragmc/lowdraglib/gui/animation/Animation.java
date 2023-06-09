package com.lowdragmc.lowdraglib.gui.animation;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import com.lowdragmc.lowdraglib.utils.interpolate.IEase;
import com.lowdragmc.lowdraglib.utils.interpolate.Interpolator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/9/8
 * @implNote Animation
 */
public abstract class Animation {

    protected Widget widget;
    protected Interpolator interpolator;
    protected long duration = 250;
    protected long delay = 0;
    protected IEase ease = Eases.EaseLinear;
    protected Runnable onFinish;
    private float time = 0;
    private boolean in;
    private boolean isFinish;
    private long startTick = -1;
    private boolean init = false;

    public Animation setWidget(Widget widget) {
        this.widget = widget;
        return this;
    }

    protected void onUpdate(Number number) {
        time = number.floatValue();
    }

    protected float getTime() {
        return time;
    }

    protected void onFinish(Number number) {
        widget.setActive(true);
        interpolator = null;
        isFinish = true;
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public boolean isFinish() {
        return isFinish;
    }

    public Widget getWidget() {
        return widget;
    }

    public Animation setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public Animation setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public Animation setEase(IEase ease) {
        this.ease = ease;
        return this;
    }

    public Animation setOnFinish(Runnable onFinish) {
        if (this.onFinish != null) {
            Runnable last = this.onFinish;
            this.onFinish = () -> {
                last.run();
                onFinish.run();
            };
        } else {
            this.onFinish = onFinish;
        }
        return this;
    }

    public Runnable getOnFinish() {
        return onFinish;
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

    protected float getTick() {
        if (!init) {
            init = true;
            interpolator = new Interpolator(0, 1, duration, ease, this::onUpdate, this::onFinish);
            startTick = System.currentTimeMillis();
            widget.setActive(false);
        }
        return (System.currentTimeMillis() - startTick);
    }

    @Environment(EnvType.CLIENT)
    public void pre(@Nonnull GuiGraphics graphics) {

    }

    @Environment(EnvType.CLIENT)
    public void post(@Nonnull GuiGraphics graphics) {

    }

    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float tickTime = getTick();
        if (tickTime >= delay) {
            if (interpolator != null) {
                interpolator.update(tickTime);
            }
            if (widget != null) {
                pre(graphics);
                widget.drawInBackground(graphics, mouseX, mouseY, partialTicks);
                post(graphics);
            }
        } else if (isOut()) {
            widget.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Environment(EnvType.CLIENT)
    public void drawInForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float tickTime = getTick();
        if (tickTime >= delay) {
            if (interpolator != null) {
                interpolator.update(tickTime);
            }
            if (widget != null) {
                pre(graphics);
                widget.drawInForeground(graphics, mouseX, mouseY, partialTicks);
                post(graphics);
            }
        } else if (isOut()) {
            widget.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        }
    }

}
