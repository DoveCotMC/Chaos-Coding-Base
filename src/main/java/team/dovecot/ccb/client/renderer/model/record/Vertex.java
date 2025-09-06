package team.dovecot.ccb.client.renderer.model.record;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Objects;

public final class Vertex {
    private final Vector3d pos;
    private final Vector3d normal;
    private final Vector2d uv;

    public Vertex(Vector3d pos, Vector3d normal, Vector2d uv) {
        this.pos = pos;
        this.normal = normal;
        this.uv = uv;
    }

    public Vector3d pos() {
        return pos;
    }

    public Vector3d normal() {
        return normal;
    }

    public Vector2d uv() {
        return uv;
    }
}
