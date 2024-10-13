package com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.data;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.editor.ui.sceneeditor.sceneobject.ISceneObject;
import com.lowdragmc.lowdraglib.syncdata.IPersistedSerializable;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2024/06/26
 * @implNote A transform that represents the position, rotation, and scale of a scene object.
 */
@Accessors(fluent = true)
public final class Transform implements IPersistedSerializable, IConfigurable {
    /**
     * Position of the transform relative to the parent transform.
     */
    @Getter
    @Configurable(name = "transform.position", tips = "transform.position.tips")
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE})
    private Vector3f localPosition = new Vector3f();

    /**
     * Rotation of the transform relative to the parent transform.
     */
    @Getter
    @Configurable(name = "transform.rotation", tips = "transform.rotation.tips", forceUpdate = false)
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE})
    private Quaternionf localRotation = new Quaternionf();

    /**
     * Scale of the transform relative to the parent transform.
     */
    @Getter
    @Configurable(name = "transform.scale", tips = "transform.scale.tips")
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE})
    private Vector3f localScale = new Vector3f(1, 1, 1);

    /**
     * The parent transform of the transform.
     */
    @Nullable
    @Getter
    private Transform parent;

    /**
     * The children transforms of the transform.
     */
    @Getter
    private List<Transform> children = new ArrayList<>();

    /**
     * The transform owner.
     */
    @Getter
    @Nonnull
    private final ISceneObject sceneObject;

    // runtime
    @Nullable
    private Vector3f position = null;
    @Nullable
    private Quaternionf rotation = null;
    @Nullable
    private Vector3f scale = null;
    @Nullable
    private Matrix4f localTransformMatrix = null;
    @Nullable
    private Matrix4f worldToLocalMatrix = null;
    private Matrix4f localToWorldMatrix = null;

    public Transform(ISceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }


    /**
     * Notify the transform that the transform has changed.
     * This will clean cache of the world space position, rotation, scale, and matrices.
     */
    private void onTransformChanged() {
        position = null;
        rotation = null;
        scale = null;
        localTransformMatrix = null;
        worldToLocalMatrix = null;
        localToWorldMatrix = null;
        for (Transform child : children) {
            child.onTransformChanged();
        }
        sceneObject.onTransformChanged();
    }

    /**
     * Set the parent transform of the transform.
     */
    public void parent(@Nullable Transform parent) {
        if (this.parent == parent) {
            return;
        }
        if (this.parent != null) {
            this.parent.children.remove(this);
            this.parent.sceneObject.onChildChanged();
        }
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
            this.sceneObject.setScene(parent.sceneObject.getScene());
            parent.sceneObject.onChildChanged();
        }
        var lastPosition = position();
        var lastRotation = rotation();
        var lastScale = scale();
        onTransformChanged();
        position(lastPosition);
        rotation(lastRotation);
        scale(lastScale);
    }

    /**
     * Matrix that represents the local transform of the transform.
     */
    public Matrix4f localTransformMatrix() {
        if (localTransformMatrix == null) {
            localTransformMatrix = new Matrix4f().translate(localPosition).rotate(localRotation).scale(localScale);
        }
        return localTransformMatrix;
    }

    /**
     * Matrix that transforms from local space to world space.
     */
    public Matrix4f localToWorldMatrix() {
        if (worldToLocalMatrix == null) {
            worldToLocalMatrix = parent == null ?
                    localTransformMatrix() :
                    new Matrix4f(parent.localToWorldMatrix()).mul(localTransformMatrix());
        }
        return worldToLocalMatrix;
    }

    /**
     * Matrix that transforms from world space to local space.
     */
    public Matrix4f worldToLocalMatrix() {
        if (localToWorldMatrix == null) {
            localToWorldMatrix = localToWorldMatrix().invert(new Matrix4f());
        }
        return localToWorldMatrix;
    }

    /**
     * Set the position, rotation, and scale of the transform.
     */
    public Transform set(Transform transform) {
        position(transform.position());
        rotation(transform.rotation());
        scale(transform.scale());
        return this;
    }

    /**
     * The world space position of the transform.
     */
    public Vector3f position() {
        if (position == null) {
            position = parent == null ?
                    localPosition :
                    parent.localToWorldMatrix().transformPosition(new Vector3f(localPosition));
        }
        return new Vector3f(position);
    }

    public void position(Vector3f position) {
        onTransformChanged();
        this.position = new Vector3f(position);
        if (parent == null) {
            this.localPosition = new Vector3f(position);
        } else {
            this.localPosition = parent.worldToLocalMatrix().transformPosition(new Vector3f(position));
        }
    }

    @ConfigSetter(field = "localPosition")
    public void localPosition(Vector3f localPosition) {
        this.localPosition = localPosition;
        onTransformChanged();
    }

    /**
     * The world space rotation of the transform.
     */
    public Quaternionf rotation() {
        if (rotation == null) {
            rotation = parent == null ?
                    localRotation :
                    parent.rotation().mul(localRotation);
        }
        return new Quaternionf(rotation);
    }

    public void rotation(Quaternionf rotation) {
        onTransformChanged();
        this.rotation = new Quaternionf(rotation);
        if (parent == null) {
            this.localRotation = new Quaternionf(rotation);
        } else {
            this.localRotation = parent.rotation().invert().mul(rotation);
        }
    }

    @ConfigSetter(field = "localRotation")
    public void localRotation(Quaternionf localRotation) {
        this.localRotation = localRotation;
        onTransformChanged();
    }

    /**
     * The world space scale of the transform.
     */
    public Vector3f scale() {
        if (scale == null) {
            scale = parent == null ?
                    localScale :
                    new Vector3f(localScale).mul(parent.scale());
        }
        return new Vector3f(scale);
    }

    public void scale(Vector3f scale) {
        onTransformChanged();
        this.scale = new Vector3f(scale);
        if (parent == null) {
            this.localScale = new Vector3f(scale);
        } else {
            this.localScale = new Vector3f(scale).div(parent.scale());
        }
    }

    @ConfigSetter(field = "localScale")
    public void localScale(Vector3f localScale) {
        this.localScale = localScale;
        onTransformChanged();
    }
}
