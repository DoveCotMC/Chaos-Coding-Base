package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Map;

// TODO: Currently just wrapping VertexBuffer...
public class UploadedModel {
    private final Map<String, VertexBuffer> buffer;
    private final Map<String, AbstractTexture> texture;
    private final Map<String, UploadedModel> children;

    private UploadedModel(Map<String, VertexBuffer> buffer, Map<String, AbstractTexture> texture, Map<String, UploadedModel> children) {
        this.buffer = buffer;
        this.texture = texture;
        this.children = children;
    }

    public static UploadedModel upload(LocalModel model) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.BLOCK);

//        builder.vertex();
//
//        ChunkRenderDispatcher
//        LevelRenderer.renderChunkLayer

        BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vertexBuffer.upload(renderedBuffer);

        return new UploadedModel(vertexBuffer, null, new ArrayList<>());
    }

    public void release() {
//        this.buffer.bind();
//        this.buffer.close();
//        this.texture.releaseId();
    }

    public void drawWithShader() {
        this.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    public void drawWithShader(ShaderInstance shaderInstance) {
        this.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderInstance);
    }

    public void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shaderInstance) {
//        this.buffer.drawWithShader(modelViewMatrix, projectionMatrix, shaderInstance);
    }
}
