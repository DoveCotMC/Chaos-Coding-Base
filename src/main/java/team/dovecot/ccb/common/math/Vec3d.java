package team.dovecot.ccb.common.math;

import java.util.Objects;

public class Vec3d {
    private double x;
    private double y;
    private double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3d add(Vec3d vec3d) {
        return add(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d add(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;

        return this;
    }

    public Vec3d subtract(Vec3d vec3d) {
        return subtract(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Vec3d multiply(Vec3d vec3d) {
        return multiply(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;

        return this;
    }

    public Vec3d divide(Vec3d vec3d) {
        return add(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d divide(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vec3d vec3d = (Vec3d) o;
        return Double.compare(x, vec3d.x) == 0 && Double.compare(y, vec3d.y) == 0 && Double.compare(z, vec3d.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
