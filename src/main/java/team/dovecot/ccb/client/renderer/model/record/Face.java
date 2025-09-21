package team.dovecot.ccb.client.renderer.model.record;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
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

    public List<Face> asTriangles() {
        if (numVertices() <= 3)
            return List.of(this);

        List<Face> faces = new ArrayList<>();
        for (int i = 2; i < numVertices(); i++) {
            faces.add(
                    new Face(
                            List.of(
                                    vertices.get(0),
                                    vertices.get(i - 1),
                                    vertices.get(i)
                            )
                    )
            );
        }
        return faces;
    }

    @Override
    public String toString() {
        return "Face{" +
                "vertices=" + vertices +
                '}';
    }
}
