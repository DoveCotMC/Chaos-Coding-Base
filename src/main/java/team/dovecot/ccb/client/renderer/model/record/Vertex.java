package team.dovecot.ccb.client.renderer.model.record;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.text.NumberFormat;
import java.util.Objects;

public final class Vertex {
    private final Vector3f pos;
    private final Vector2f uv;
    private final Vector3f normal;

    public Vertex(Vector3f pos, Vector2f uv, Vector3f normal) {
        this.pos = pos;
        this.uv = uv;
        this.normal = normal;
    }

    public Vector3f pos() {
        return pos;
    }

    public Vector2f uv() {
        return uv;
    }

    public Vector3f normal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "pos=" + pos.toString(NumberFormat.getInstance()) +
                ", uv=" + uv.toString(NumberFormat.getInstance()) +
                ", normal=" + normal.toString(NumberFormat.getInstance()) +
                '}';
    }
}
