package team.dovecotmc.ccb.client.boundingbox;

import org.joml.Vector3d;

public class OBB {
    private final Vector3d center;
    private final Vector3d halfExtents;
    private final Vector3d[] axes;

    public OBB(Vector3d center, Vector3d halfExtents, Vector3d[] axes) {
        this.center = new Vector3d(center);
        this.halfExtents = new Vector3d(halfExtents);
        this.axes = new Vector3d[3];
        for (int i = 0; i < 3; i++) {
            this.axes[i] = new Vector3d(axes[i]).normalize();
        }
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

    public boolean intersects(OBB other) {
        Vector3d[] A = this.axes;
        Vector3d[] B = other.axes;

        double[] a = {halfExtents.x, halfExtents.y, halfExtents.z};
        double[] b = {other.halfExtents.x, other.halfExtents.y, other.halfExtents.z};

        double[][] R = new double[3][3];
        double[][] AbsR = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R[i][j] = A[i].dot(B[j]);
                AbsR[i][j] = Math.abs(R[i][j]) + 1e-6f; // 防止浮点误差
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
}
