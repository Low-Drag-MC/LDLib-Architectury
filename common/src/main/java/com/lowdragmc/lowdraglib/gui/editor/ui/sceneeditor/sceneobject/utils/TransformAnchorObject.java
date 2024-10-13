package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.utils;

import com.lowdragmc.lowdraglib.client.shader.LDLibRenderTypes;
import com.lowdragmc.lowdraglib.client.utils.RenderBufferUtils;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Ray;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneInteractable;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneRendering;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.SceneObject;
import com.lowdragmc.lowdraglib.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class TransformAnchorObject extends SceneObject implements ISceneRendering, ISceneInteractable {
    private static final VoxelShape xAxisCollider = Shapes.box(0, -0.1, -0.1, 1.2, 0.1, 0.1);
    private static final VoxelShape yAxisCollider = Shapes.box(-0.1, 0, -0.1, 0.1, 1.2, 0.1);
    private static final VoxelShape zAxisCollider = Shapes.box(-0.1, -0.1, 0, 0.1, 0.1, 1.2);

    @Nullable
    @Setter
    @Getter
    private Transform targetTransform;

    //runtime
    private boolean isMovingX, isMovingY, isMovingZ;
    private Vector3f clickPos, initialPos;
    private Vector2f start, end; // start - end refers to the line segment of the axis, initial refers to the initial position of the click

    public boolean isHoverAxis(Direction.Axis axis) {
        var scene = getScene();
        if (scene == null) {
            return false;
        }
        return scene.getMouseRay()
                .map(ray -> ray.localToWorld(transform()).toInfinite())
                .map(ray -> switch (axis) {
                    case X -> ray.clip(xAxisCollider) != null;
                    case Y -> ray.clip(yAxisCollider) != null;
                    case Z -> ray.clip(zAxisCollider) != null;
                }).orElse(false);
    }

    @Override
    public void updateFrame(float partialTicks) {
        super.updateFrame(partialTicks);
        var scene = getScene();
        if (scene == null) {
            return;
        }
        if (isMovingX || isMovingY || isMovingZ) {
            var direction = new Vector3f(isMovingX ? 1 : 0, isMovingY ? 1 : 0, isMovingZ ? 1 : 0);
            var transformMatrix = transform().worldToLocalMatrix();
            if (start == null) {
                start = scene.project(transformMatrix.transformPosition(direction.mul(5, new Vector3f())));
                end = scene.project(transformMatrix.transformPosition(direction.mul(-5, new Vector3f())));
                var clicked = getPerpendicularFoot(start, end, new Vector2f(scene.getLastMouseX(), scene.getLastMouseY()));
                var clickRay = scene.unProject((int) clicked.x, (int) clicked.y);
                initialPos = transform().position();
                clickPos = findClosestPoints(
                        initialPos,
                        transformMatrix.transformDirection(direction, new Vector3f()),
                        clickRay.startPos(), clickRay.getDirection())[0];
            }

            var current = getPerpendicularFoot(start, end, new Vector2f(scene.getLastMouseX(), scene.getLastMouseY()));
            var ray = scene.unProject((int) current.x, (int) current.y);
            var closestPoint = findClosestPoints(
                    initialPos,
                    transformMatrix.transformDirection(direction, new Vector3f()),
                    ray.startPos(), ray.getDirection())[0];

            var destination = initialPos.add(closestPoint.sub(clickPos), new Vector3f());

            if (targetTransform != null) {
                targetTransform.position(destination);
            } else {
                transform().position(destination);
            }
        }

    }

    @Override
    public void draw(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        poseStack.pushPose();
        poseStack.mulPoseMatrix(new Matrix4f().translate(transform().position()).rotate(transform().rotation()));
        drawInternal(poseStack, bufferSource, partialTicks);
        poseStack.popPose();
    }

    @Override
    public void drawInternal(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        var buffer = bufferSource.getBuffer(LDLibRenderTypes.noDepthLines());
        RenderSystem.lineWidth(3);
        var pose = poseStack.last().pose();
        var hoverColor = 0xFFFFFFFF;
        var xColor = !isHoverAxis(Direction.Axis.X) ? 0xFFFF0000 : hoverColor;
        var yColor = !isHoverAxis(Direction.Axis.Y) ? 0xFF00FF00 : hoverColor;
        var zColor = !isHoverAxis(Direction.Axis.Z) ? 0xFF0000FF : hoverColor;
        var xR = ColorUtils.red(xColor);
        var xG = ColorUtils.green(xColor);
        var xB = ColorUtils.blue(xColor);
        var xA = ColorUtils.alpha(xColor);
        var yR = ColorUtils.red(yColor);
        var yG = ColorUtils.green(yColor);
        var yB = ColorUtils.blue(yColor);
        var yA = ColorUtils.alpha(yColor);
        var zR = ColorUtils.red(zColor);
        var zG = ColorUtils.green(zColor);
        var zB = ColorUtils.blue(zColor);
        var zA = ColorUtils.alpha(zColor);
        // draw x axis
        RenderBufferUtils.drawLine(pose, buffer, new Vector3f(0, 0, 0), new Vector3f(1, 0, 0),
                xR, xG, xB, xA, xR, xG, xB, xA);
        if (isMovingX) {
            RenderBufferUtils.drawLine(pose, buffer, new Vector3f(-50, 0, 0), new Vector3f(50, 0, 0),
                    xR, xG, xB, xA, xR, xG, xB, xA);
        }
        // draw y axis
        RenderBufferUtils.drawLine(pose, buffer, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0),
                yR, yG, yB, yA, yR, yG, yB, yA);
        if (isMovingY) {
            RenderBufferUtils.drawLine(pose, buffer, new Vector3f(0, -50, 0), new Vector3f(0, 50, 0),
                    yR, yG, yB, yA, yR, yG, yB, yA);
        }
        // draw z axis
        RenderBufferUtils.drawLine(pose, buffer, new Vector3f(0, 0, 0), new Vector3f(0, 0, 1),
                zR, zG, zB, zA, zR, zG, zB, zA);
        if (isMovingZ) {
            RenderBufferUtils.drawLine(pose, buffer, new Vector3f(0, 0, -50), new Vector3f(0, 0, 50),
                    zR, zG, zB, zA, zR, zG, zB, zA);
        }

        // draw arrow
        buffer = bufferSource.getBuffer(LDLibRenderTypes.positionColorNoDepth());
        // draw x arrow
        RenderBufferUtils.shapeCone(poseStack, buffer, 1, 0, 0, 0.05f, 0.15f, 10,
                xR, xG, xB, xA, Direction.Axis.X);
        RenderBufferUtils.shapeCircle(poseStack, buffer, 1, 0, 0, 0.05f, 10,
                xR, xG, xB, xA, Direction.Axis.X);
        // draw y arrow
        RenderBufferUtils.shapeCone(poseStack, buffer, 0, 1, 0, 0.05f, 0.15f, 10,
                yR, yG, yB, yA, Direction.Axis.Y);
        RenderBufferUtils.shapeCircle(poseStack, buffer, 0, 1, 0, 0.05f, 10,
                yR, yG, yB, yA, Direction.Axis.Y);
        // draw z arrow
        RenderBufferUtils.shapeCone(poseStack, buffer, 0, 0, 1, 0.05f, 0.15f, 10,
                zR, zG, zB, zA, Direction.Axis.Z);
        RenderBufferUtils.shapeCircle(poseStack, buffer, 0, 0, 1, 0.05f, 10,
                zR, zG, zB, zA, Direction.Axis.Z);
    }


    @Override
    public boolean onMouseClick(Ray mouseRay) {
        if (isHoverAxis(Direction.Axis.X)) {
            isMovingX = true;
            return true;
        } else if (isHoverAxis(Direction.Axis.Y)) {
            isMovingY = true;
            return true;
        } else if (isHoverAxis(Direction.Axis.Z)) {
            isMovingZ = true;
            return true;
        }
        return false;
    }

    @Override
    public void onMouseRelease(Ray mouseRay) {
        isMovingX = false;
        isMovingY = false;
        isMovingZ = false;
        start = null;
        end = null;
        clickPos = null;
    }

    public static Vector2f getPerpendicularFoot(Vector2f start, Vector2f end, Vector2f outpoint) {
        float x1 = start.x, y1 = start.y;
        float x2 = end.x, y2 = end.y;
        float x3 = outpoint.x, y3 = outpoint.y;

        if (x2 - x1 < 1f) {
            return new Vector2f(x1, y3);
        } else {
            // calculate the slope of the line
            var k = (y2 - y1) / (x2 - x1);

            // calculate the perpendicular foot
            var dX = (k * k * x3 + k * (y3 - y1 + k * x1) - k * y1 + x3) / (k * k + 1);
//            var dX = x3;
            var dY = k * (dX - x1) + y1;

            return new Vector2f(dX, dY);
        }
    }

    public static Vector3f[] findClosestPoints(Vector3f A1, Vector3f d1, Vector3f A2, Vector3f d2) {
        Vector3f w0 = new Vector3f(A1).sub(A2);

        float a = d1.dot(d1); // d1·d1
        float b = d1.dot(d2); // d1·d2
        float c = d2.dot(d2); // d2·d2
        float d = d1.dot(w0); // d1·(A1-A2)
        float e = d2.dot(w0); // d2·(A1-A2)

        float denominator = a * c - b * b;

        float t1, t2;

        if (denominator != 0) {
            t1 = (b * e - c * d) / denominator;
            t2 = (a * e - b * d) / denominator;
        } else {
            // Handle parallel case where denominator is 0
            t1 = 0;
            t2 = d / b; // or another consistent solution for parallel lines
        }

        Vector3f closestPointOnLine1 = new Vector3f(d1).mul(t1).add(A1);
        Vector3f closestPointOnLine2 = new Vector3f(d2).mul(t2).add(A2);

        return new Vector3f[]{closestPointOnLine1, closestPointOnLine2};
    }
}
