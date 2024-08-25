package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject;

import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.SceneEditorWidget;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data.Transform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2024/06/26
 * @implNote A scene object that can be placed in the scene editor.
 */
@Environment(EnvType.CLIENT)
public interface ISceneObject {
    /**
     * Get the scene.
     */
    @Nullable
    SceneEditorWidget getScene();

    /**
     * Set the scene internal. you should not call this method directly.
     */
    void setSceneInternal(SceneEditorWidget scene);

    /**
     * Set the scene. you should not call this method directly.
     */
    default void setScene(SceneEditorWidget scene) {
        if (getScene() != scene) {
            setSceneInternal(scene);
            children().forEach(child -> child.setScene(scene));
        }
    }

    /**
     * Get the transform of the object.
     */
    Transform transform();

    /**
     * Destroy the object.
     */
    default void destroy() {
        if (transform().parent() == null) {
            Optional.ofNullable(getScene()).ifPresent(scene -> scene.removeSceneObject(this));
        } else {
            transform().parent(null);
        }
    }

    /**
     * Get the children of the object. (read-only)
     * if possible, please cache the children list. and update it when the children is changed. see {@link #onChildChanged()}
     */
    default List<ISceneObject> children() {
        return transform().children().stream().map(Transform::sceneObject).toList();
    }

    /**
     * Called when the transform of the object is changed.
     */
    default void onTransformChanged() {
    }

    /**
     * Called when the children of the object is changed.
     */
    default void onChildChanged() {
    }

    /**
     * Update the interactable per tick.
     */
    default void updateTick() {
    }

    /**
     * Update the interactable per frame.
     */
    default void updateFrame(float partialTicks) {
    }

    /**
     * Execute the consumer for the object and all children.
     */
    default void executeAll(Consumer<ISceneObject> consumer) {
        consumer.accept(this);
        children().forEach(child -> child.executeAll(consumer));
    }

    /**
     * Execute the consumer for the object and all children.
     */
    default void executeAll(Consumer<ISceneObject> consumer, @Nullable Consumer<ISceneObject> before, @Nullable Consumer<ISceneObject> after) {
        if (before != null) before.accept(this);
        consumer.accept(this);
        children().forEach(child -> child.executeAll(consumer));
        if (after != null) after.accept(this);
    }
}
