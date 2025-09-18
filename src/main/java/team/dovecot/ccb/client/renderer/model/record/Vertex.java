package team.dovecot.ccb.client.renderer.model.record;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Objects;

public final class Vertex {
    private final Vector3d pos;
    private final Vector2d uv;
    private final Vector3d normal;

    public Vertex(Vector3d pos, Vector2d uv, Vector3d normal) {
        this.pos = pos;
        this.uv = uv;
        this.normal = normal;
    }

    public Vector3d pos() {
        return pos;
    }

    public Vector2d uv() {
        return uv;
    }

    public Vector3d normal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "pos=" + pos +
                ", uv=" + uv +
                ", normal=" + normal +
                '}';
    }
}
