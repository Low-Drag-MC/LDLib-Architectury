package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.utils;

import com.lowdragmc.lowdraglib.client.shader.LDLibRenderTypes;
import com.lowdragmc.lowdraglib.client.utils.RenderBufferUtils;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneInteractable;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneRendering;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.SceneObject;
import com.lowdragmc.lowdraglib.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class SizeBoxObject extends SceneObject implements ISceneRendering, ISceneInteractable {
    private static final VoxelShape xAxisCollider = Shapes.box(0, -0.1, -0.1, 1.2, 0.1, 0.1);
    private static final VoxelShape yAxisCollider = Shapes.box(-0.1, 0, -0.1, 0.1, 1.2, 0.1);
    private static final VoxelShape zAxisCollider = Shapes.box(-0.1, -0.1, 0, 0.1, 0.1, 1.2);

    @Nullable
    @Setter
    @Getter
    private Transform targetTransform;

    @Override
    public void draw(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        poseStack.pushPose();
        poseStack.mulPose(new Matrix4f().translate(transform().position()).rotate(transform().rotation()));
        drawInternal(poseStack, bufferSource, partialTicks);
        poseStack.popPose();
    }

    @Override
    public void drawInternal(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        var buffer = bufferSource.getBuffer(LDLibRenderTypes.noDepthLines());
        RenderSystem.lineWidth(3);
        // draw the size box
        var color = 0xff00ff00;
        var r = ColorUtils.red(color);
        var g = ColorUtils.green(color);
        var b = ColorUtils.blue(color);
        var a = ColorUtils.alpha(color);
        RenderBufferUtils.drawCubeFrame(poseStack, buffer, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, r, g, b, a);

        // draw control plane
        buffer = bufferSource.getBuffer(LDLibRenderTypes.positionColorNoDepth());
        drawPlane(poseStack, buffer, Direction.UP, r, g, b, a);
        drawPlane(poseStack, buffer, Direction.DOWN, r, g, b, a);
        drawPlane(poseStack, buffer, Direction.NORTH, r, g, b, a);
        drawPlane(poseStack, buffer, Direction.SOUTH, r, g, b, a);
        drawPlane(poseStack, buffer, Direction.WEST, r, g, b, a);
        drawPlane(poseStack, buffer, Direction.EAST, r, g, b, a);
    }

    private void drawPlane(PoseStack poseStack, VertexConsumer buffer, Direction side, float r, float g, float b, float a) {
        switch (side) {
            case UP -> RenderBufferUtils.drawCubeFace(poseStack, buffer, -0.33f, 0.5f, -0.33f, 0.33f, 0.5f, 0.33f, r, g, b, a, false);
            case DOWN -> RenderBufferUtils.drawCubeFace(poseStack, buffer, -0.33f, -0.5f, -0.33f, 0.33f, -0.5f, 0.33f, r, g, b, a, false);
            case NORTH -> RenderBufferUtils.drawCubeFace(poseStack, buffer, -0.33f, 0.33f, -0.5f, 0.33f, 0.67f, -0.5f, r, g, b, a, false);
            case SOUTH -> RenderBufferUtils.drawCubeFace(poseStack, buffer, -0.33f, 0.33f, 0.5f, 0.33f, 0.67f, 0.5f, r, g, b, a, false);
            case WEST -> RenderBufferUtils.drawCubeFace(poseStack, buffer, -0.5f, 0.33f, -0.33f, -0.5f, 0.67f, 0.33f, r, g, b, a, false);
            case EAST -> RenderBufferUtils.drawCubeFace(poseStack, buffer, 0.5f, 0.33f, -0.33f, 0.5f, 0.67f, 0.33f, r, g, b, a, false);
        }
    }

}
