package com.lowdragmc.lowdraglib.gui.compass.component.animation;

import com.lowdragmc.lowdraglib.utils.XmlUtils;
import lombok.Getter;
import org.w3c.dom.Element;

/**
 * @author KilaBash
 * @date 2023/7/28
 * @implNote Action
 */
public abstract class Action {

    @Getter
    protected int delay;
    @Getter
    protected boolean startBeforeLast;

    public Action(Element element) {
        delay = XmlUtils.getAsInt(element, "delay", 0);
        startBeforeLast = XmlUtils.getAsBoolean(element, "start-before-last", false);
    }

    public abstract int getDuration();


    public abstract void performAction(AnimationFrame frame, CompassScene scene, boolean anima);
}
