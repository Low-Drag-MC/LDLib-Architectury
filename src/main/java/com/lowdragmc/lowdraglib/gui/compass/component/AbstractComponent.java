package com.lowdragmc.lowdraglib.gui.compass.component;

import com.lowdragmc.lowdraglib.gui.compass.ILayoutComponent;
import com.lowdragmc.lowdraglib.gui.compass.LayoutPageWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.XmlUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.w3c.dom.Element;

/**
 * @author KilaBash
 * @date 2022/9/4
 * @implNote AbstractComponent
 */
public abstract class AbstractComponent implements ILayoutComponent {
    protected int topMargin = 0;
    protected int bottomMargin = 0;
    protected int leftMargin = 0;
    protected int rightMargin = 0;
    protected String hoverInfo;

    @Override
    public ILayoutComponent fromXml(Element element) {
        this.topMargin = XmlUtils.getAsInt(element, "top-margin", topMargin);
        this.bottomMargin = XmlUtils.getAsInt(element, "bottom-margin", bottomMargin);
        this.leftMargin = XmlUtils.getAsInt(element, "left-margin", leftMargin);
        this.rightMargin = XmlUtils.getAsInt(element, "right-margin", rightMargin);
        this.hoverInfo = XmlUtils.getAsString(element, "hover-info", null);
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final LayoutPageWidget createWidgets(LayoutPageWidget currentPage) {
        if (topMargin > 0) {
            currentPage = currentPage.addOffsetSpace(topMargin);
        }
        currentPage = addWidgets(currentPage);
        if (bottomMargin > 0) {
            currentPage = currentPage.addOffsetSpace(bottomMargin);
        }
        return currentPage;
    }

    protected int width(LayoutPageWidget currentPage) {
        return currentPage.getPageWidth() - leftMargin - rightMargin;
    }

    protected Widget wrapper(Widget widget) {
        if (leftMargin != 0 || rightMargin != 0) {
            var group = new WidgetGroup();
            group.addWidget(widget);
            widget.setSelfPosition(new Position(leftMargin, 0));
            group.setSize(new Size(widget.getSize().width + leftMargin + rightMargin, widget.getSize().height));
            return group;
        }
        return widget;
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract LayoutPageWidget addWidgets(LayoutPageWidget currentPage);
}
