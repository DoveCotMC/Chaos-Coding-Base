package team.dovecot.ccb.client.renderer.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector3d;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.nio.ByteBuffer;
import java.util.*;

public class LocalModel {
    private final Map<String, List<Face>> faces;
    // TODO: Vanilla texture manager
    private final Map<String, ResourceLocation> texture;
    private final Map<String, LocalModel> children;
    private final boolean isRoot;

    private LocalModel(Map<String, List<Face>> faces, Map<String, ResourceLocation> texture, Map<String, LocalModel> children, boolean isRoot) {
        this.faces = faces;
        this.texture = texture;
        this.children = children;
        this.isRoot = isRoot;
    }

    public LocalModel(Map<String, LocalModel> children) {
        this(new HashMap<>(), new HashMap<>(), children, true);
    }

    public Map<String, ResourceLocation> getTexture() {
        return texture;
    }

    public Map<String, List<Face>> getFaces() {
        return faces;
    }

    public Map<String, LocalModel> getChildren() {
        return children;
    }

    public int getNumFaces() {
        return faces.size();
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

    @Override
    public String toString() {
        return "LocalModel{" +
                "faces=" + faces +
                ", texture=" + texture +
                ", children=" + children +
                ", isRoot=" + isRoot +
                '}';
    }

    public static class Builder {
        private final String name;
//        private TODO: How to store faces?
        private String material;
        private final Map<String, Builder> children;
        private final Map<String, ResourceLocation> textures;
        private final Map<String, List<Face>> faces;

        private final Deque<Builder> stack;
        @Nullable
        private Builder parent;

        private Builder() {
            this("", null);
        }

        public Builder(String name, Builder parent) {
            this.name = name;
            this.textures = new HashMap<>();
            this.material = "";
            this.children = new HashMap<>();
            this.faces = new HashMap<>();
            this.stack = new ArrayDeque<>();
            this.parent = parent;
        }

        public static Builder empty() {
            return new Builder();
        }

        public Builder pushGroup(String groupName) {
            Builder childBuilder = new Builder(groupName, this);
            stack.push(childBuilder);
            return childBuilder;
        }

        public Builder popGroup() {
            if (this.parent == null)
                throw new IllegalArgumentException("You are trying to pop root builder...");

            this.parent.children.put(name, this);
            return this.parent;
        }

        public Builder addMaterial(String material, ResourceLocation texture) {
            this.material = material;
            this.textures.put(material, texture);
            return this;
        }

        public Builder addFaces(List<Face> faces) {
            this.faces.put(material, faces);
            return this;
        }

        public LocalModel build() {
            Map<String, LocalModel> children = new HashMap<>();
            for (Map.Entry<String, Builder> entry : this.children.entrySet()) {
                String groupName = entry.getKey();
                Builder builder = entry.getValue();
                children.put(groupName, builder.build());
            }

            return new LocalModel(faces, textures, children, parent == null);
        }
    }
}
