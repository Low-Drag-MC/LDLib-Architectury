package com.lowdragmc.lowdraglib.utils;


import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote ShapeUtils
 */
public class ShapeUtils {

    public static AABB rotate(AABB AABB, Direction facing) {
        switch (facing) {
            case SOUTH -> {
                return rotate(AABB, new Vector3(0, 1, 0), 180);
            }
            case EAST -> {
                return rotate(AABB, new Vector3(0, 1, 0), -90);
            }
            case WEST -> {
                return rotate(AABB, new Vector3(0, 1, 0), 90);
            }
            case UP -> {
                return rotate(AABB, new Vector3(1, 0, 0), 90);
            }
            case DOWN -> {
                return rotate(AABB, new Vector3(1, 0, 0), -90);
            }
        }
        return AABB;
    }

    public static AABB rotate(AABB AABB, Vector3 axis, double degree) {
        Vector3 min = new Vector3(AABB.minX, AABB.minY, AABB.minZ).subtract(0.5);
        Vector3 max = new Vector3(AABB.maxX, AABB.maxY, AABB.maxZ).subtract(0.5);
        double radians = Math.toRadians(degree);
        min.rotate(radians, axis);
        max.rotate(radians, axis);
        min.add(0.5);
        max.add(0.5);
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static VoxelShape rotate(VoxelShape shape, Direction facing) {
        return shape.toAabbs().stream().map(AABB -> Shapes.create(rotate(AABB, facing))).reduce(Shapes.empty(), Shapes::or);
    }

    public static VoxelShape rotate(VoxelShape shape, Vector3 axis, double degree) {
        return shape.toAabbs().stream().map(AABB -> Shapes.create(rotate(AABB, axis, degree))).reduce(Shapes.empty(), Shapes::or);
    }
}
