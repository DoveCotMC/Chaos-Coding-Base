package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.util.*;

public class LocalModel {
    private final ResourceLocation location;
    private final Map<String, List<Face>> faces;
    // TODO: Vanilla texture manager
    private final Map<String, ResourceLocation> texture;
    private final Map<String, LocalModel> children;
    private final boolean isRoot;

    private LocalModel(ResourceLocation location, Map<String, List<Face>> faces, Map<String, ResourceLocation> texture, Map<String, LocalModel> children, boolean isRoot) {
        this.location = location;
        this.faces = faces;
        this.texture = texture;
        this.children = children;
        this.isRoot = isRoot;
    }

    public LocalModel(ResourceLocation location, Map<String, LocalModel> children) {
        this(location, new HashMap<>(), new HashMap<>(), children, true);
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

    public void consume(String materialName, VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        for (Face nonTriangleFace : getFaces().get(materialName)) {
            for (Face face : nonTriangleFace.asTriangles()) {
                for (Vertex vertex : face.vertices()) {
                    // Add vertex
                    consumer.vertex(
                            pose,
                            vertex.pos().x(),
                            vertex.pos().y(),
                            vertex.pos().z()
                    ).color(
                            1f,
                            1f,
                            1f,
                            1f
                    ).uv(
                            // TODO: Configurable flipV?
                            vertex.uv().x(),
                            vertex.uv().y()
                    ).overlayCoords(
                            overlay
                    ).uv2(
                            light & 0xFFFF,
                            light >> 16 & 0xFFFF
                    ).normal(
                            normal,
                            vertex.normal().x(),
                            vertex.normal().y(),
                            vertex.normal().z()
                    ).endVertex();
                }
            }
        }
    }

    public ResourceLocation getLocation() {
        return location;
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
        private final ResourceLocation location;
        private final String name;
//        private TODO: How to store faces?
        private String material;
        private final Map<String, Builder> children;
        private final Map<String, ResourceLocation> textures;
        private final Map<String, List<Face>> faces;

        private final Deque<Builder> stack;
        @Nullable
        private Builder parent;

        private Builder(ResourceLocation location) {
            this(location, "", null);
        }

        public Builder(ResourceLocation location, String name, Builder parent) {
            this.location = location;
            this.name = name;
            this.textures = new HashMap<>();
            this.material = "";
            this.children = new HashMap<>();
            this.faces = new HashMap<>();
            this.stack = new ArrayDeque<>();
            this.parent = parent;
        }

        public static Builder create(ResourceLocation location) {
            return new Builder(location);
        }

        public Builder pushGroup(String groupName) {
            Builder childBuilder = new Builder(location, groupName, this);
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

            return new LocalModel(location, faces, textures, children, parent == null);
        }
    }
}
