package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.*;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote Icons
 */
public class Icons {
    public static ResourceTexture LEFT = new ResourceTexture("ldlib:textures/gui/left.png");
    public static ResourceTexture UP = new ResourceTexture("ldlib:textures/gui/up.png");
    public static ResourceTexture DOWN = new ResourceTexture("ldlib:textures/gui/down.png");
    public static ResourceTexture RIGHT = new ResourceTexture("ldlib:textures/gui/right.png");
    public static ResourceTexture RESOURCE = icon("resource");
    public static ResourceTexture PALETTE = icon("palette");
    public static ResourceTexture RESOURCE_SETTING = icon("resource_setting");
    public static ResourceTexture WIDGET_SETTING = icon("widget_setting");
    public static ResourceTexture WIDGET_BASIC = icon("widget_basic");
    public static ResourceTexture WIDGET_GROUP = icon("widget_group");
    public static ResourceTexture WIDGET_CONTAINER = icon("widget_container");
    public static ResourceTexture WIDGET_CUSTOM = icon("widget_custom");
    public static ResourceTexture ADD = icon("add");
    public static ResourceTexture SAVE = icon("save");
    public static ResourceTexture HELP = icon("help");
    public static ResourceTexture COPY = icon("copy");
    public static ResourceTexture PASTE = icon("paste");
    public static ResourceTexture CUT = icon("cut");
    public static ResourceTexture REMOVE = icon("remove");
    public static ResourceTexture DELETE = icon("delete");
    public static ResourceTexture EXPORT = icon("export");
    public static ResourceTexture IMPORT = icon("import");
    public static ResourceTexture OPEN_FILE = icon("open_file");
    public static ResourceTexture ADD_FILE = icon("add_file");
    public static ResourceTexture EDIT_FILE = icon("edit_file");
    public static ResourceTexture REMOVE_FILE = icon("remove_file");
    public static ResourceTexture CHECK = icon("check");
    public static ResourceTexture HISTORY = icon("history");

    //align
    public static ResourceTexture ALIGN_H_C = icon("align_horizontal_center");
    public static ResourceTexture ALIGN_H_D = icon("align_horizontal_distribute");
    public static ResourceTexture ALIGN_H_L = icon("align_horizontal_left");
    public static ResourceTexture ALIGN_H_R = icon("align_horizontal_right");
    public static ResourceTexture ALIGN_V_C = icon("align_vertical_center");
    public static ResourceTexture ALIGN_V_D = icon("align_vertical_distribute");
    public static ResourceTexture ALIGN_V_T = icon("align_vertical_top");
    public static ResourceTexture ALIGN_V_B = icon("align_vertical_bottom");


    private static ResourceTexture icon(String name) {
        return new ResourceTexture("ldlib:textures/gui/icon/%s.png".formatted(name));
    }

    public static IGuiTexture borderText(int border, String text, int color) {
        return new GuiTextureGroup(new ColorBorderTexture(border, color), new TextTexture(text, color).transform(1, 1));
    }

    public static IGuiTexture borderText(String text) {
        return borderText(1, text, -1);
    }

}
