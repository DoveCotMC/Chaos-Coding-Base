package team.dovecot.ccb.client.renderer.model.record;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.List;
import java.util.Objects;

public final class Face {
    private final List<Vertex> vertices;

    public Face(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> vertices() {
        return vertices;
    }

    public int numVertices() {
        return vertices.size();
    }

    @Override
    public String toString() {
        return "Face{" +
                "vertices=" + vertices +
                '}';
    }
}
