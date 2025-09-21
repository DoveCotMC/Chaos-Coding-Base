package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import team.dovecot.ccb.client.renderer.model.LocalModel;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedVertexBuffer {
    private final Map<String, VertexBuffer> buffers;
    private final Map<String, ResourceLocation> textures;
    private final Map<String, WrappedVertexBuffer> children;

    public WrappedVertexBuffer(Map<String, VertexBuffer> buffers, Map<String, ResourceLocation> textures, Map<String, WrappedVertexBuffer> children) {
        this.buffers = buffers;
        this.textures = textures;
        this.children = children;
    }

    static boolean bl = false;
    public static WrappedVertexBuffer upload(LocalModel localModel, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        RenderSystem.assertOnRenderThread();

        Map<String, WrappedVertexBuffer> children = new HashMap<>();
        Map<String, VertexBuffer> buffers = new HashMap<>();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        for (Map.Entry<String, List<Face>> entry : localModel.getFaces().entrySet()) {
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            String materialName = entry.getKey();
            List<Face> faces = entry.getValue();

            for (Face nonTriangleFace : faces) {
                for (Face face : nonTriangleFace.asTriangles()) {
                    if (!bl)
                        System.out.println(face);
                    for (Vertex vertex : face.vertices()) {
                        // Add vertex
                        builder.vertex(
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
                                OverlayTexture.NO_OVERLAY
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

//            builder.vertex(pose, 0, 0, 0).color(1f, 1f, 1f, 1f).uv(0f, 0f).endVertex();
//            builder.vertex(pose, 1, 0, 0).color(1f, 1f, 1f, 1f).uv(1f, 0f).endVertex();
//            builder.vertex(pose, 0.5f, 1, 0).color(1f, 1f, 1f, 1f).uv(0.5f, 1f).endVertex();

//            builder.vertex(pose, -0.09f, 0.94f, 0.94f).color(1f, 1f, 1f, 1f).uv(0f, 0f).endVertex();
//            builder.vertex(pose, 0, 0, 0).color(1f, 1f, 1f, 1f).uv(1f, 0f).endVertex();
//            builder.vertex(pose, 0.1f, 0, 0).color(1f, 1f, 1f, 1f).uv(0.5f, 1f).endVertex();


            // Finalize and Upload
            BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            buffer.bind();
            buffer.upload(renderedBuffer);
            buffers.put(materialName, buffer);
        }

        for (Map.Entry<String, LocalModel> entry : localModel.getChildren().entrySet()) {
            String name = entry.getKey();
            LocalModel child = entry.getValue();
            children.put(name, upload(child, pose, normal, light, overlay));
        }

        bl = true;
        return new WrappedVertexBuffer(buffers, localModel.getTexture(), children);
    }

    public void render(int light) {
        for (Map.Entry<String, VertexBuffer> entry : this.buffers.entrySet()) {
            String material = entry.getKey();
            VertexBuffer vertexBuffer = entry.getValue();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, this.textures.get(material));

            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutShader);
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

            // The index of Lightmap UV or UV2 is 4
            vertexBuffer.bind();
//            GL30.glDisableVertexAttribArray(4);
//
//            int lightProcessed = 0xF00000;
////            lightProcessed = light;
//
//            // Insert Lightmap UV
//            GL30.glVertexAttribI2i(4, lightProcessed & 0xFFFF, lightProcessed >> 16 & 0xFFFF);
//
            vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
//            GL30.glEnableVertexAttribArray(4);
        }
    }

    public void renderAll(int light) {
        this.render(light);
        for (WrappedVertexBuffer buffer : this.children.values()) {
            buffer.renderAll(light);
        }
    }

    private void release() {
        for (VertexBuffer buffer : this.buffers.values()) {
            buffer.close();
        }
    }

    public void releaseAll() {
        this.release();
        for (WrappedVertexBuffer child : this.children.values()) {
            child.release();
        }
    }
}
