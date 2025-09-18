package team.dovecot.ccb.client.renderer.model;

import org.joml.Vector2d;
import org.joml.Vector3d;
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

    public static LocalModel parse(List<Vector3d> positions, List<Vector2d> uvs, List<Vector3d> normals, Map<String, Map<String, List<List<Integer>>>> faceIndices) {
        for (String group : faceIndices.keySet()) {
            System.out.println(faceIndices.get(group));
        }
        return null;
    }

    public int getNumFaces() {
        return faces.size();
    }
}
