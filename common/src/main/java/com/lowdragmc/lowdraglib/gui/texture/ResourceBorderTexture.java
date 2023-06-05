package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@LDLRegister(name = "border_texture", group = "texture")
public class ResourceBorderTexture extends ResourceTexture {
    public static final ResourceBorderTexture BORDERED_BACKGROUND = new ResourceBorderTexture("ldlib:textures/gui/bordered_background.png", 195, 136, 4, 4);
    public static final ResourceBorderTexture BORDERED_BACKGROUND_BLUE = new ResourceBorderTexture("ldlib:textures/gui/bordered_background_blue.png", 195, 136, 4, 4);
    public static final ResourceBorderTexture BUTTON_COMMON = new ResourceBorderTexture("ldlib:textures/gui/button_common.png", 198, 18, 1, 1);
    public static final ResourceBorderTexture BAR = new ResourceBorderTexture("ldlib:textures/gui/button_common.png", 180, 20, 1, 1);

    @Configurable(tips = {"ldlib.gui.editor.tips.corner_size.0", "ldlib.gui.editor.tips.corner_size.1"}, collapse = false)
    public Size boderSize;

    @Configurable(tips = "ldlib.gui.editor.tips.image_size", collapse = false)
    public Size imageSize;

    public ResourceBorderTexture() {
        this("ldlib:textures/gui/bordered_background_blue.png", 195, 136, 4, 4);
    }

    public ResourceBorderTexture(String imageLocation, int imageWidth, int imageHeight, int cornerWidth, int cornerHeight) {
        super(imageLocation);
        boderSize = new Size(cornerWidth, cornerHeight);
        imageSize = new Size(imageWidth, imageHeight);
    }

    @Override
    public ResourceBorderTexture setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawSubAreaInternal(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //compute relative sizes
        float cornerWidth = boderSize.width * 1f / imageSize.width;
        float cornerHeight = boderSize.height * 1f / imageSize.height;
        //draw up corners
        super.drawSubAreaInternal(stack, x, y, boderSize.width, boderSize.height, 0, 0, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(stack, x + width - boderSize.width, y, boderSize.width, boderSize.height, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubAreaInternal(stack, x, y + height - boderSize.height, boderSize.width, boderSize.height, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(stack, x + width - boderSize.width, y + height - boderSize.height, boderSize.width, boderSize.height, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        super.drawSubAreaInternal(stack, x + boderSize.width, y, width - 2 * boderSize.width, boderSize.height,
                cornerWidth, 0, 1 - 2 * cornerWidth, cornerHeight);
        super.drawSubAreaInternal(stack, x + boderSize.width, y + height - boderSize.height, width - 2 * boderSize.width, boderSize.height,
                cornerWidth, 1 - cornerHeight, 1 - 2 * cornerWidth, cornerHeight);
        //draw vertical connections
        super.drawSubAreaInternal(stack, x, y + boderSize.height, boderSize.width, height - 2 * boderSize.height,
                0, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        super.drawSubAreaInternal(stack, x + width - boderSize.width, y + boderSize.height, boderSize.width, height - 2 * boderSize.height,
                1 - cornerWidth, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        //draw central body
        super.drawSubAreaInternal(stack, x + boderSize.width, y + boderSize.height,
                width - 2 * boderSize.width, height - 2 * boderSize.height,
                cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight);
    }

    @Environment(EnvType.CLIENT)
    protected void drawGuides(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        new ColorBorderTexture(-1, 0xffff0000).draw(stack, 0, 0,
                x + width * offsetX, y + height * offsetY,
                (int) (width * imageWidth), (int) (height * imageHeight));

        float cornerWidth = boderSize.width * 1f / imageSize.width;
        float cornerHeight = boderSize.height * 1f / imageSize.height;

        new ColorBorderTexture(-1, 0xff00ff00).draw(stack, 0, 0,
                x, y, (int) (width * cornerWidth), (int) (height * cornerHeight));
    }
}
