package team.dovecot.ccb.client.renderer.model;

import org.joml.Vector2d;
import org.joml.Vector3d;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.nio.ByteBuffer;
import java.util.*;

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
        private final String name;
//        private TODO: How to store faces?
        private String material;
        private final Map<String, LocalModel> children;

        private final Deque<Builder> stack;
        private Builder parent;

        public Builder() {
            this("", null);
        }

        public Builder(String name, Builder parent) {
            this.name = name;
            this.material = "";
            this.children = new HashMap<>();
            this.stack = new ArrayDeque<>();
            this.parent = parent;
        }

        public Builder pushGroup(String groupName) {
            Builder childBuilder = new Builder(groupName, this);
            stack.push(childBuilder);
            return childBuilder;
        }

        public Builder popGroup() {
            if (this.parent == null)
                throw new IllegalArgumentException("You are trying to pop a root builder...");

            this.parent.children.put(name, this.build());
            return this.parent;
        }

        public Builder setMaterial(String material) {
            this.material = material;
            return this;
        }

        public Builder addFaces(List<Face> faces) {
            // TODO: Faces ToT
            return this;
        }

        public LocalModel build() {
            return new LocalModel(new HashMap<>());
        }
    }
}
