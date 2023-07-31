package com.lowdragmc.lowdraglib.gui.compass.component;

import com.lowdragmc.lowdraglib.gui.compass.ILayoutComponent;
import com.lowdragmc.lowdraglib.gui.compass.LayoutPageWidget;
import com.lowdragmc.lowdraglib.gui.compass.component.animation.AnimationFrame;
import com.lowdragmc.lowdraglib.gui.compass.component.animation.CompassScene;
import com.lowdragmc.lowdraglib.utils.XmlUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/7/28
 * @implNote AnimationComponent
 */
public class CompassComponent extends AbstractComponent {

    List<AnimationFrame> frames = new ArrayList<>();
    boolean useScene = true;
    boolean tickScene = false;
    float zoom = 6;
    int height = 250;

    @Override
    public ILayoutComponent fromXml(Element element) {
        var frameNodes = element.getElementsByTagName("frame");
        for (int i = 0; i < frameNodes.getLength(); i++) {
            var frameElement = (Element) frameNodes.item(i);
            frames.add(new AnimationFrame(i, frameElement));
        }
        useScene = XmlUtils.getAsBoolean(element, "scene", useScene);
        zoom = XmlUtils.getAsFloat(element, "zoom", zoom);
        height = XmlUtils.getAsInt(element, "height", height);
        tickScene = XmlUtils.getAsBoolean(element, "tick-scene", tickScene);
        bottomMargin = 10;
        topMargin = 10;
        return super.fromXml(element);
    }

    @Override
    protected LayoutPageWidget addWidgets(LayoutPageWidget currentPage) {
        return currentPage.addStreamWidget(new CompassScene(currentPage.getPageWidth(), height, useScene, tickScene, zoom, frames));
    }

}
