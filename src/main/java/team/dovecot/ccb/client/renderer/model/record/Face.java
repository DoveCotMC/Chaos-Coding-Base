package team.dovecot.ccb.client.renderer.model.record;

import java.util.List;
import java.util.Objects;

public final class Face {
    private final List<Integer> verticesIndex;

    public Face(List<Integer> verticesIndex) {
        this.verticesIndex = verticesIndex;
    }

    public List<Integer> verticesIndex() {
        return verticesIndex;
    }

    public int numVertices() {
        return verticesIndex.size();
    }

    @Override
    public String toString() {
        return "Face{" +
                "verticesIndex=" + verticesIndex +
                '}';
    }
}
