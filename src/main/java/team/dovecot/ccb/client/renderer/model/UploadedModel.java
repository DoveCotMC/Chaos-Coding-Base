package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.AbstractTexture;

// TODO: Currently just wrapping VertexBuffer...
public class UploadedModel {
    private final VertexBuffer buffer;
    private final AbstractTexture texture;

    private UploadedModel(VertexBuffer buffer, AbstractTexture texture) {
        this.buffer = buffer;
        this.texture = texture;
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

        return new UploadedModel(vertexBuffer, null);
    }

    public void release() {
        this.buffer.bind();
        this.buffer.close();
        this.texture.releaseId();
    }
}
