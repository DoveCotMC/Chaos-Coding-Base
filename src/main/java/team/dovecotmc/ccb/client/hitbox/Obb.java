package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;

public class Obb {
    private Vector3d center;
    private Vector3d halfExtents;
    private Vector3d[] axes;

    public Obb(Vector3d center, Vector3d halfExtents) {
        this.center = new Vector3d(center);
        this.halfExtents = new Vector3d(halfExtents);
        this.axes = new Vector3d[]{
                new Vector3d(1, 0, 0),
                new Vector3d(0, 1, 0),
                new Vector3d(0, 0, 1)
        };
    }

    public Vector3d getCenter() {
        return new Vector3d(center);
    }

    public Vector3d getHalfExtents() {
        return new Vector3d(halfExtents);
    }

    public Vector3d[] getAxes() {
        Vector3d[] copy = new Vector3d[3];
        for (int i = 0; i < 3; i++) copy[i] = new Vector3d(axes[i]);
        return copy;
    }

    public Vector3d[] getVertices() {
        Vector3d[] vertices = new Vector3d[8];
        int idx = 0;
        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    Vector3d corner = new Vector3d(center);
                    corner.fma(x * halfExtents.x, axes[0]);
                    corner.fma(y * halfExtents.y, axes[1]);
                    corner.fma(z * halfExtents.z, axes[2]);
                    vertices[idx++] = corner;
                }
            }
        }
        return vertices;
    }

    public void setRotation(Quaterniond rotation) {
        Matrix3d rotMat = new Matrix3d().rotation(rotation);
        setRotation(rotMat);
    }

    public void setRotation(Matrix3d rotMat) {
        for (int i = 0; i < 3; i++) {
            rotMat.transform(axes[i]);
            axes[i].normalize();
        }
    }

    public void applyTransform(Matrix4d transform) {
        Matrix3d rotScale = new Matrix3d();
        transform.get3x3(rotScale);

        Vector3d[] newAxes = new Vector3d[3];
        Vector3d newHalfExtents = new Vector3d();

        for (int i = 0; i < 3; i++) {
            Vector3d axis = new Vector3d();
            rotScale.getColumn(i, axis);
            double scale = axis.length();
            axis.normalize();
            newAxes[i] = axis;
            newHalfExtents.setComponent(i, halfExtents.get(i) * scale);
        }

        Vector3d newCenter = new Vector3d(center);
        transform.transformPosition(newCenter);

        this.center = newCenter;
        this.axes = newAxes;
        this.halfExtents = newHalfExtents;
    }

    public boolean contains(Vector3d point) {
        Vector3d dir = new Vector3d(point).sub(center);

        for (int i = 0; i < 3; i++) {
            double dist = dir.dot(axes[i]);
            if (Math.abs(dist) > halfExtents.get(i))
                return false;
        }
        return true;
    }

    public boolean intersects(Obb other) {
        Vector3d[] A = this.axes;
        Vector3d[] B = other.axes;

        double[] a = { halfExtents.x, halfExtents.y, halfExtents.z };
        double[] b = { other.halfExtents.x, other.halfExtents.y, other.halfExtents.z };

        double[][] R = new double[3][3];
        double[][] AbsR = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R[i][j] = A[i].dot(B[j]);
                AbsR[i][j] = Math.abs(R[i][j]) + 1e-9;
            }
        }

        Vector3d tVec = new Vector3d(other.center).sub(this.center);
        double[] t = {
                tVec.dot(A[0]),
                tVec.dot(A[1]),
                tVec.dot(A[2])
        };

        double ra, rb;

        for (int i = 0; i < 3; i++) {
            ra = a[i];
            rb = b[0] * AbsR[i][0] + b[1] * AbsR[i][1] + b[2] * AbsR[i][2];
            if (Math.abs(t[i]) > ra + rb) return false;
        }

        for (int i = 0; i < 3; i++) {
            ra = a[0] * AbsR[0][i] + a[1] * AbsR[1][i] + a[2] * AbsR[2][i];
            rb = b[i];
            double proj = Math.abs(t[0] * R[0][i] + t[1] * R[1][i] + t[2] * R[2][i]);
            if (proj > ra + rb) return false;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ra = a[(i + 1) % 3] * AbsR[(i + 2) % 3][j] + a[(i + 2) % 3] * AbsR[(i + 1) % 3][j];
                rb = b[(j + 1) % 3] * AbsR[i][(j + 2) % 3] + b[(j + 2) % 3] * AbsR[i][(j + 1) % 3];
                double proj = Math.abs(
                        t[(i + 2) % 3] * R[(i + 1) % 3][j] -
                                t[(i + 1) % 3] * R[(i + 2) % 3][j]
                );
                if (proj > ra + rb) return false;
            }
        }

        return true;
    }

    public @Nullable Vector3d rayIntersect(Vector3d rayOrigin, Vector3d rayDirection) {
        if (Minecraft.getInstance().player == null)
            return null;

        rayOrigin.fma(0.01, rayDirection);

        Vector3d p = new Vector3d(center).sub(rayOrigin);
        double tMin = -Double.MAX_VALUE;
        double tMax = Double.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            double e = axes[i].dot(p);
            double f = rayDirection.dot(axes[i]);
            double he = halfExtents.get(i);

            if (Math.abs(f) > 1e-9) {
                double t1 = (e + he) / f;
                double t2 = (e - he) / f;
                if (t1 > t2) {
                    double tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }

                if (t1 > tMin) tMin = t1;
                if (t2 < tMax) tMax = t2;

                if (tMin > tMax) return null;
                if (tMax < 0) return null;
            } else {
                if (-e - he > 0 || -e + he < 0) return null;
            }
        }

        double hitDistance = (tMin >= 0) ? tMin : tMax;
        double maxPickDistance = Minecraft.getInstance().player.isCreative() ? 5.0 : 4.5;

        if (Minecraft.getInstance().player != null && hitDistance > maxPickDistance)
            return null;

        return new Vector3d(rayDirection).mul(hitDistance).add(rayOrigin);
    }

    public Matrix4f getTransformMatrix4f() {
        Matrix4f transform = new Matrix4f();

        transform.translate((float) center.x, (float) center.y, (float) center.z);

        Vector3d xAxis = axes[0];
        Vector3d yAxis = axes[1];
        Vector3d zAxis = new Vector3d(axes[2]).negate(); // ← 关键：反转 Z 轴

        Matrix4f rotation = new Matrix4f().set(
                (float) xAxis.x, (float) yAxis.x, (float) zAxis.x, 0.0f,
                (float) xAxis.y, (float) yAxis.y, (float) zAxis.y, 0.0f,
                (float) xAxis.z, (float) yAxis.z, (float) zAxis.z, 0.0f,
                0.0f,           0.0f,           0.0f,           1.0f
        );

        transform.mul(rotation);

        transform.scale((float) halfExtents.x, (float) halfExtents.y, (float) halfExtents.z);

        return transform;
    }
}
