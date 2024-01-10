package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ColorPattern
 */
public enum ColorPattern {
    WHITE(0xffffffff),
    T_WHITE(0x88ffffff),
    BLACK(0xff222222),
    T_BLACK(0x44222222),
    GRAY(0xff666666),
    T_GRAY(0x66666666),
    GREEN(0xff33ff00),
    T_GREEN(0x8833ff00),
    RED(0xff9d0122),
    T_RED(0x889d0122),
    YELLOW(0xffffff33),
    T_YELLOW(0x88ffff33),
    CYAN(0xff337777),
    T_CYAN(0x88337777),
    ;
    public final int color;

    ColorPattern(int color) {
        this.color = color;
    }

    public ColorRectTexture rectTexture() {
        return new ColorRectTexture(color);
    }

    public ColorBorderTexture borderTexture(int border) {
        return new ColorBorderTexture(border, color);
    }

}
