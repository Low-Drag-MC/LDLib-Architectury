package com.lowdragmc.lowdraglib.utils;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Size {

    public static final Size ZERO = new Size(0, 0);

    public final int width;
    public final int height;

    public Size(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    public static Size of(int width, int height) {
        return new Size(width, height);
    }

    public static Size add(Position position) {
        return new Size(position.x, position.y);
    }

    public Size add(Size other) {
        return new Size(width + other.width, height + other.height);
    }

    public Size add(int width, int height) {
        return new Size(this.width + width, this.height + height);
    }

    public Size subtract(Size other) {
        return new Size(width - other.width, height - other.height);
    }

    public Size addWidth(int width) {
        return new Size(this.width + width, height);
    }

    public Size addHeight(int height) {
        return new Size(width, this.height + height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;
        Size size = (Size) o;
        return width == size.width &&
                height == size.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("width", width)
                .add("height", height)
                .toString();
    }
}
