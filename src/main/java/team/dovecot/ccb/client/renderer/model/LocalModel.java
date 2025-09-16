package team.dovecot.ccb.client.renderer.model;

import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class LocalModel {
    private final List<Vertex> vertices;
    private final Map<String, List<Face>> faces;
    private final Map<String, ByteBuffer> texture;

    private final Map<String, LocalModel> children;

    public LocalModel(List<Vertex> vertices, Map<String, List<Face>> faces, Map<String, ByteBuffer> texture, Map<String, LocalModel> children) {
        this.vertices = vertices;
        this.faces = faces;
        this.texture = texture;
        this.children = children;
    }

    public int getNumFaces() {
        return faces.size();
    }
}
