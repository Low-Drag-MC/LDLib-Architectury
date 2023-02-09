package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@RegisterUI(name = "group_texture", group = "texture")
public class GuiTextureGroup extends TransformTexture{

    @Configurable(collapse = false)
    public IGuiTexture[] textures;

    public GuiTextureGroup() {
        this(ResourceBorderTexture.BORDERED_BACKGROUND, new ResourceTexture());
    }

    public GuiTextureGroup(IGuiTexture... textures) {
        this.textures = textures;
    }

    public GuiTextureGroup setTextures(IGuiTexture[] textures) {
        this.textures = textures;
        return this;
    }

    @Override
    public GuiTextureGroup setColor(int color) {
        for (IGuiTexture texture : textures) {
            texture.setColor(color);
        }
        return this;
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        for (IGuiTexture texture : textures) {
            texture.draw(stack, mouseX,mouseY,  x, y, width, height);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void updateTick() {
        for (IGuiTexture texture : textures) {
            texture.updateTick();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawSubAreaInternal(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        for (IGuiTexture texture : textures) {
            texture.drawSubArea(stack, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        }
    }

    @Override
    public void setUIResource(Resource<IGuiTexture> texturesResource) {
        for (IGuiTexture texture : textures) {
            texture.setUIResource(texturesResource);
        }
    }
}
