package com.lowdragmc.lowdraglib.gui.compass;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import lombok.Getter;
import lombok.Setter;

/**
 * @author KilaBash
 * @date 2023/7/28
 * @implNote CompassConfig
 */
public class CompassConfig {

    @Getter @Setter
    public IGuiTexture listViewBackground = ResourceBorderTexture.BORDERED_BACKGROUND_INVERSE;
    @Getter @Setter
    public IGuiTexture listItemBackground = ResourceBorderTexture.BUTTON_COMMON;
    @Getter @Setter
    public IGuiTexture listItemSelectedBackground = ResourceBorderTexture.BUTTON_COMMON.copy().setColor(0xff337f7f);
    @Getter @Setter
    public IGuiTexture nodeBackground = ResourceBorderTexture.BUTTON_COMMON;
    @Getter @Setter
    public IGuiTexture nodeHoverBackground = ResourceBorderTexture.BUTTON_COMMON.copy().setColor(0xff337f7f);
}
