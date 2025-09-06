package team.dovecot.ccb.client.renderer.model.record;

import java.util.List;
import java.util.Objects;

public final class Face {
    private final List<Vertex> vertices;
    private final int numVertices;

    public Face(List<Vertex> vertices, int numVertices) {
        this.vertices = vertices;
        this.numVertices = numVertices;
    }

    public List<Vertex> vertices() {
        return vertices;
    }

    public int numVertices() {
        return numVertices;
    }
}
