//package com.lowdragmc.lowdraglib.utils;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Vec3i;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.phys.Vec3;
//import org.joml.Vector3d;
//import org.joml.Vector3f;
//import org.joml.Vector4f;
//
//import java.math.BigDecimal;
//import java.math.MathContext;
//import java.math.RoundingMode;
//
//public class Vector3f {
//    public static final Vector3f X = new Vector3f(1, 0, 0);
//    public static final Vector3f Y = new Vector3f(0, 1, 0);
//    public static final Vector3f Z = new Vector3f(0, 0, 1);c
//    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
//    public static final Vector3f ONE = new Vector3f(1, 1, 1);
//
//    public double x;
//    public double y;
//    public double z;
//
//    public Vector3f(double d, double d1, double d2) {
//        this.x = d;
//        this.y = d1;
//        this.z = d2;
//    }
//
//    public Vector3f(Vector3f vec) {
//        this.x = vec.x;
//        this.y = vec.y;
//        this.z = vec.z;
//    }
//
//    public Vector3f(Vector3f vec) {
//        this.x = vec.x();
//        this.y = vec.y();
//        this.z = vec.z();
//    }
//
//    public Vector3f(Vec3 vec) {
//        this.x = vec.x();
//        this.y = vec.y();
//        this.z = vec.z();
//    }
//
//    public Vector3f(Vector3d vec) {
//        this.x = vec.x;
//        this.y = vec.y;
//        this.z = vec.z;
//    }
//
//    public Vector3f(Vec3i vec) {
//        this.x = vec.getX();
//        this.y = vec.getY();
//        this.z = vec.getZ();
//    }
//
//    public static Vector3f fromNBT(CompoundTag tag) {
//        return new Vector3f(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
//    }
//
//    public Vec3 vec3() {
//        return new Vec3(this.x, this.y, this.z);
//    }
//
//    public BlockPos pos() {
//        return new BlockPos((int) this.x, (int) this.y, (int) this.z);
//    }
//
//    public Vector3f rotate(double angle, Vector3f axis) {
//        Quat.aroundAxis(axis.copy().normalize(), angle).rotate(this);
//        return this;
//    }
//
//    public Vector3f rotateYXY(Vector3f rotation) {
//        return rotate(rotation.y, Y).rotate(rotation.x, X).rotate(rotation.z, Y);
//    }
//
//    public double angle(Vector3f vec) {
//        return Math.acos(this.copy().normalize().dotProduct(vec.copy().normalize()));
//    }
//
//    public double dotProduct(Vector3f vec) {
//        double d = vec.x * this.x + vec.y * this.y + vec.z * this.z;
//        if (d > 1.0D && d < 1.00001D) {
//            d = 1.0D;
//        } else if (d < -1.0D && d > -1.00001D) {
//            d = -1.0D;
//        }
//
//        return d;
//    }
//
//    public CompoundTag writeToNBT(CompoundTag tag) {
//        tag.putDouble("x", this.x);
//        tag.putDouble("y", this.y);
//        tag.putDouble("z", this.z);
//        return tag;
//    }
//
//    public Vector3f vector3f() {
//        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
//    }
//
//    public Vector4f vector4f() {
//        return new Vector4f((float)this.x, (float)this.y, (float)this.z, 1.0F);
//    }
//
//    public Vector3f set(double x1, double y1, double z1) {
//        this.x = x1;
//        this.y = y1;
//        this.z = z1;
//        return this;
//    }
//
//    public Vector3f set(Vector3f vec) {
//        return this.set(vec.x, vec.y, vec.z);
//    }
//
//    public Vector3f add(double dx, double dy, double dz) {
//        this.x += dx;
//        this.y += dy;
//        this.z += dz;
//        return this;
//    }
//
//    public Vector3f add(double d) {
//        return this.add(d, d, d);
//    }
//
//    public Vector3f add(Vector3f vec) {
//        return this.add(vec.x, vec.y, vec.z);
//    }
//
//    public Vector3f add(BlockPos pos) {
//        return this.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
//    }
//
//    public Vector3f subtract(double dx, double dy, double dz) {
//        this.x -= dx;
//        this.y -= dy;
//        this.z -= dz;
//        return this;
//    }
//
//    public Vector3f subtract(double d) {
//        return this.subtract(d, d, d);
//    }
//
//    public Vector3f subtract(Vector3f vec) {
//        return this.subtract(vec.x, vec.y, vec.z);
//    }
//
//    public Vector3f subtract(BlockPos pos) {
//        return this.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
//    }
//
//    public Vector3f multiply(double fx, double fy, double fz) {
//        this.x *= fx;
//        this.y *= fy;
//        this.z *= fz;
//        return this;
//    }
//
//    public Vector3f multiply(double f) {
//        return this.multiply(f, f, f);
//    }
//
//    public Vector3f multiply(Vector3f f) {
//        return this.multiply(f.x, f.y, f.z);
//    }
//
//    public Vector3f divide(double fx, double fy, double fz) {
//        this.x /= fx;
//        this.y /= fy;
//        this.z /= fz;
//        return this;
//    }
//
//    public Vector3f divide(double f) {
//        return this.divide(f, f, f);
//    }
//
//    public Vector3f divide(Vector3f vec) {
//        return this.divide(vec.x, vec.y, vec.z);
//    }
//
//    public Vector3f divide(BlockPos pos) {
//        return this.divide((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
//    }
//
//    public Vector3f floor() {
//        this.x = Math.floor(this.x);
//        this.y = Math.floor(this.y);
//        this.z = Math.floor(this.z);
//        return this;
//    }
//
//    public Vector3f ceil() {
//        this.x = Math.ceil(this.x);
//        this.y = Math.ceil(this.y);
//        this.z = Math.ceil(this.z);
//        return this;
//    }
//
//    public double mag() {
//        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
//    }
//
//    public double magSquared() {
//        return this.x * this.x + this.y * this.y + this.z * this.z;
//    }
//
//    public Vector3f xCrossProduct() {
//        double d = this.z;
//        double d1 = -this.y;
//        this.x = 0.0D;
//        this.y = d;
//        this.z = d1;
//        return this;
//    }
//
//    public Vector3f zCrossProduct() {
//        double d = -this.y;
//        double d1 = this.x;
//        this.x = d;
//        this.y = d1;
//        this.z = 0.0D;
//        return this;
//    }
//
//    @Override
//    public int hashCode() {
//        long l = Double.doubleToLongBits(this.x);
//        int i = (int)(l ^ l >>> 32);
//        l = Double.doubleToLongBits(this.y);
//        i = 31 * i + (int)(l ^ l >>> 32);
//        l = Double.doubleToLongBits(this.z);
//        i = 31 * i + (int)(l ^ l >>> 32);
//        return i;
//    }
//
//    public boolean equals(Object o) {
//        if (!(o instanceof Vector3f)) {
//            return false;
//        } else {
//            Vector3f v = (Vector3f)o;
//            return this.x == v.x && this.y == v.y && this.z == v.z;
//        }
//    }
//
//    public boolean equalsT(Vector3f v) {
//        return between(this.x - 1.0E-5D, v.x, this.x + 1.0E-5D) && between(this.y - 1.0E-5D, v.y, this.y + 1.0E-5D) && between(this.z - 1.0E-5D, v.z, this.z + 1.0E-5D);
//    }
//
//    public static boolean between(double min, double value, double max) {
//        return min <= value && value <= max;
//    }
//
//    public Vector3f copy() {
//        return new Vector3f(this);
//    }
//
//    public String toString() {
//        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
//        return "Vector3f(" + new BigDecimal(this.x, cont) + ", " + new BigDecimal(this.y, cont) + ", " + new BigDecimal(this.z, cont) + ")";
//    }
//
//    public Vector3f normalize() {
//        double d = mag();
//        if (d != 0) {
//            multiply(1 / d);
//        }
//        return this;
//    }
//
//    public Vector3f project(Vector3f b) {
//        double l = b.magSquared();
//        if (l == 0.0D) {
//            this.set(0.0D, 0.0D, 0.0D);
//        } else {
//            double m = this.dotProduct(b) / l;
//            this.set(b).multiply(m);
//        }
//        return this;
//    }
//
//    public Vector3f crossProduct(Vector3f vec) {
//        double d = this.y * vec.z - this.z * vec.y;
//        double d1 = this.z * vec.x - this.x * vec.z;
//        double d2 = this.x * vec.y - this.y * vec.x;
//        this.x = d;
//        this.y = d1;
//        this.z = d2;
//        return this;
//    }
//
//    public boolean isZero() {
//        return this.x == 0 && this.y ==0 && this.z == 0;
//    }
//
//    public double min() {
//        return x < y ? Math.min(x, z) : Math.min(y, z);
//    }
//
//    public double max() {
//        return x > y ? Math.max(x, z) : Math.max(y, z);
//    }
//
//}
