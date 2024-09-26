package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2024/06/26
 * @implNote A scene object that can be rendered in the scene editor.
 */
@OnlyIn(Dist.CLIENT)
public interface ISceneRendering extends ISceneObject {
    /**
     * Called before draw the object in the scene.
     * before the transform is applied. all children will be drawn after this.
     */
    default void preDraw(float partialTicks){
    }

    /**
     * Called after draw the object in the scene.
     * after the transform is applied. all children will be drawn before this.
     */
    default void postDraw(float partialTicks){
    }

    /**
     * Draw the object in the scene. execute transform here.
     */
    default void draw(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks){
        poseStack.pushPose();
        poseStack.mulPose(transform().worldToLocalMatrix());
        drawInternal(poseStack, bufferSource, partialTicks);
        poseStack.popPose();
    }

    /**
     * Draw the object in the scene.
     */
    void drawInternal(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks);
}
