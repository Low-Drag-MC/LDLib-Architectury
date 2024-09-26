package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Ray;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneInteractable;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneObject;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneRendering;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A scene which provides editable features as a unity scene.
 */
public class SceneEditorWidget extends SceneWidget {
    protected float moveSpeed = 0.1f;
    protected boolean isCameraMoving = false;
    @Nullable
    protected String screenTips = null;
    protected int tipsDuration = 0;
    @Getter
    protected Set<ISceneObject> sceneObjects = new LinkedHashSet<>();
    @Getter
    protected int lastMouseX, lastMouseY;

    public SceneEditorWidget(int x, int y, int width, int height, Level world, boolean useFBO) {
        super(x, y, width, height, world, useFBO);
        this.setRenderFacing(false);
        this.setRenderSelect(false);
    }

    public SceneEditorWidget(int x, int y, int width, int height, Level world) {
        this(x, y, width, height, world, false);
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<Ray> getMouseRay() {
        var lastHit = renderer.getLastHit();
        return lastHit == null ? Optional.empty() : Optional.of(Ray.create(renderer.getEyePos(), lastHit));
    }

    @OnlyIn(Dist.CLIENT)
    public Ray unProject(int mouseX, int mouseY) {
        var mouse = renderer.getPositionedRect(mouseX, mouseY, 0, 0);
        return new Ray(renderer.getEyePos(), renderer.unProject(mouse.position.x, mouse.position.y, false));
    }

    @OnlyIn(Dist.CLIENT)
    public Vector2f project(Vector3f pos) {
        var window = Minecraft.getInstance().getWindow();
        var result = renderer.project(pos);
        var x = result.x() * window.getGuiScaledWidth() / window.getWidth();
        var y = (window.getHeight() - result.y()) * window.getGuiScaledHeight() / window.getHeight();
        return new Vector2f(x, y);
    }


    public void setScreenTips(String tips) {
        this.screenTips = tips;
        tipsDuration = 20;
    }

    /**
     * Add a scene object to the scene root.
     */
    public void addSceneObject(ISceneObject sceneObject) {
        sceneObject.destroy();
        sceneObject.transform().parent(null);
        sceneObject.setScene(this);
        sceneObjects.add(sceneObject);
    }

    /**
     * Remove a scene object from the scene root.
     */
    public void removeSceneObject(ISceneObject sceneObject) {
        sceneObjects.remove(sceneObject);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (tipsDuration > 0) {
            tipsDuration--;
            if (tipsDuration == 0) {
                screenTips = null;
            }
        }
        for (ISceneObject sceneObject : sceneObjects) {
            sceneObject.executeAll(ISceneObject::updateTick);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (intractable && isMouseOverElement(mouseX, mouseY)) {
            if (button == 0) {
                // left click - select object
                if (getMouseRay().map(ray -> {
                    var result = new AtomicBoolean(false);
                    for (ISceneObject sceneObject : sceneObjects) {
                        sceneObject.executeAll(so -> {
                            if (so instanceof ISceneInteractable sceneInteractable) {
                                result.set(result.get() | sceneInteractable.onMouseClick(ray));
                            }
                        });
                    }
                    return result.get();
                }).orElse(false)) {
                    return true;
                }
                super.mouseClicked(mouseX, mouseY, button);
                return true;
            } else if (button == 1) {
                // right click - rotate camera
                if (draggable) {
                    isCameraMoving = true;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (intractable) {
            if (isCameraMoving) {
                // rotate camera
                if (renderer != null) {
                    var eyePos = renderer.getEyePos();
                    var lookAt = renderer.getLookAt();
                    var worldUp = renderer.getWorldUp();
                    var lookDir = new Vector3f(lookAt).sub(eyePos);
                    var cross = new Vector3f(lookDir).cross(worldUp).normalize();
                    lookDir = new Vector3f(lookDir).rotate(new Quaternionf(new AxisAngle4f((float) Math.toRadians(-dragY + 360), cross)));
                    lookDir = new Vector3f(lookDir).rotate(new Quaternionf(new AxisAngle4f((float) Math.toRadians(-dragX + 360), worldUp)));
                    center = new Vector3f(eyePos).add(new Vector3f(lookDir));
                    Vector3f pos = new Vector3f(eyePos).sub(lookAt);
                    rotationPitch = (float) Math.toDegrees(Math.atan2(pos.z, pos.x));
                    rotationYaw = (float) Math.toDegrees(Math.atan2(pos.y, Math.sqrt(pos.x * pos.x + pos.z * pos.z)));
                    renderer.setCameraLookAt(eyePos, center, worldUp);
                }
                return false;
            } else {
                getMouseRay().ifPresent(ray -> {
                    for (ISceneObject sceneObject : sceneObjects) {
                        sceneObject.executeAll(so -> {
                            if (so instanceof ISceneInteractable sceneInteractable) {
                                sceneInteractable.onMouseDrag(ray);
                            }
                        });
                    }
                });
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isCameraMoving = false;
        if (intractable) {
            if (button == 0) {
                getMouseRay().ifPresent(ray -> {
                    for (ISceneObject sceneObject : sceneObjects) {
                        sceneObject.executeAll(so -> {
                            if (so instanceof ISceneInteractable sceneInteractable) {
                                sceneInteractable.onMouseRelease(ray);
                            }
                        });
                    }
                });
                super.mouseReleased(mouseX, mouseY, button);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelX, double wheelY) {
        if (isCameraMoving) {
            if (wheelY > 0) {
                moveSpeed = Mth.clamp(moveSpeed + 0.01f, 0.02f, 10);
            } else {
                moveSpeed = Mth.clamp(moveSpeed - 0.01f, 0.02f, 10);
            }
            setScreenTips("Move Speed: x%.2f".formatted(moveSpeed));
            return true;
        }
        return super.mouseWheelMove(mouseX, mouseY, wheelX, wheelY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void renderBeforeBatchEnd(MultiBufferSource bufferSource, float partialTicks) {
        super.renderBlockOverLay(renderer);
        var poseStack = new PoseStack();
        for (ISceneObject sceneObject : sceneObjects) {
            sceneObject.executeAll(so -> so.updateFrame(partialTicks));
            sceneObject.executeAll(so -> {
                if (so instanceof ISceneRendering sceneRendering) {
                    sceneRendering.draw(poseStack, bufferSource, partialTicks);
                }
            }, so -> { // before
                if (so instanceof ISceneRendering sceneRendering) {
                    sceneRendering.preDraw(partialTicks);
                }
            }, so -> { // after
                if (so instanceof ISceneRendering sceneRendering) {
                    sceneRendering.postDraw(partialTicks);
                }
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        if (isCameraMoving && intractable) {
            var _forward = isKeyDown(GLFW.GLFW_KEY_W);
            var _backward = isKeyDown(GLFW.GLFW_KEY_S);
            var _left = isKeyDown(GLFW.GLFW_KEY_A);
            var _right = isKeyDown(GLFW.GLFW_KEY_D);
            var _up = isKeyDown(GLFW.GLFW_KEY_E);
            var _down = isKeyDown(GLFW.GLFW_KEY_Q);
            if (_forward || _backward || _left || _right || _up || _down) {
                var eyePos = renderer.getEyePos();
                var lookAt = renderer.getLookAt();
                var worldUp = renderer.getWorldUp();
                var lookDir = new Vector3f(lookAt).sub(eyePos);
                var realMoveSpeed = moveSpeed * partialTicks * (isShiftDown() ? 5 : 1);
                var forward = new Vector3f(lookDir).normalize().mul(realMoveSpeed);
                var right = new Vector3f(lookDir).cross(worldUp).normalize().mul(realMoveSpeed);
                var up = new Vector3f(worldUp).normalize().mul(realMoveSpeed);
                if (_forward) { // move forward
                    eyePos.add(forward);
                    lookAt.add(forward);
                }
                if (_backward) { // move backward
                    eyePos.sub(forward);
                    lookAt.sub(forward);
                }
                if (_left) { // move left
                    eyePos.sub(right);
                    lookAt.sub(right);
                }
                if (_right) { // move right
                    eyePos.add(right);
                    lookAt.add(right);
                }
                if (_up) { // move up
                    eyePos.add(up);
                    lookAt.add(up);
                }
                if (_down) { // move down
                    eyePos.sub(up);
                    lookAt.sub(up);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        return keyCode != GLFW.GLFW_KEY_ESCAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
        var x = getPositionX();
        var y = getPositionY();
        var width = getSizeWidth();
        var height = getSizeHeight();
        if (screenTips != null) {
            // draw screen tips
            ColorPattern.T_GRAY.rectTexture().setRadius(5)
                    .draw(graphics, mouseX, mouseY, x + width / 4f, y + height / 4f, width / 2, height / 2);
            new TextTexture(screenTips).setWidth(width).setDropShadow(false)
                    .draw(graphics, mouseX, mouseY, x + width / 4f, y + height / 4f, width / 2, height / 2);
        }
    }
}
