package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.vertex.VertexBuffer;

// TODO: Currently just wrapping VertexBuffer...
public class UploadedModel {
    private final VertexBuffer buffer;

    private UploadedModel(VertexBuffer buffer) {
        this.buffer = buffer;
    }

    public static UploadedModel upload(LocalModel model) {
        return new UploadedModel();
    }

    public void release() {
        this.buffer.bind();
        this.buffer.close();
    }
}
