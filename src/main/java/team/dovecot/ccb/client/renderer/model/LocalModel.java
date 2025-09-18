package team.dovecot.ccb.client.renderer.model;

import org.joml.Vector2d;
import org.joml.Vector3d;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalModel {
    private final Map<String, List<Face>> faces;
    private final Map<String, ByteBuffer> texture;
    private final Map<String, LocalModel> children;
    private final boolean isRoot;

    private LocalModel(Map<String, List<Face>> faces, Map<String, ByteBuffer> texture, Map<String, LocalModel> children, boolean isRoot) {
        this.faces = faces;
        this.texture = texture;
        this.children = children;
        this.isRoot = isRoot;
    }

    public LocalModel(Map<String, LocalModel> children) {
        this(new HashMap<>(), new HashMap<>(), children, true);
    }

    public static Map<String, List<Face>> parseFromRawData(List<Vector3d> positions, List<Vector2d> uvs, List<Vector3d> normals, Map<String, List<List<List<Integer>>>> indices) {
        Map<String, List<Face>> map = new HashMap<>();
        for (String material : indices.keySet()) {
            // Face iteration
            List<Face> facesWrapped = new ArrayList<>();
            for (List<List<Integer>> faceIndex : indices.get(material)) {
                List<Vertex> vertices = new ArrayList<>();
                for (List<Integer> index : faceIndex) {
                    vertices.add(new Vertex(positions.get(index.get(2)), uvs.get(index.get(1)), normals.get(index.get(2))));
                }
                facesWrapped.add(new Face(vertices));
            }
            map.put(material, facesWrapped);
        }
        return map;
    }

    public int getNumFaces() {
        return faces.size();
    }

    public static class Builder {
        private final List<String> group;
//        private TODO: How to store faces?
        private String material;

        public Builder() {
            this.group = new ArrayList<>();
            this.material = "";
        }

        public Builder pushGroup(String group) {
            this.group.add(group);
            return this;
        }

        public Builder popGroup() {
            this.group.remove(this.group.size() - 1);
            return this;
        }

        public Builder setMaterial(String material) {
            this.material = material;
            return this;
        }

        public Builder addFaces(List<Face> faces) {
            return this;
        }

        public LocalModel build() {
            return new LocalModel(new HashMap<>());
        }
    }
}
