package team.dovecot.ccb.client.renderer;

import net.minecraft.resources.ResourceLocation;
import team.dovecot.ccb.client.renderer.buffer.WrappedVertexBuffer;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private final Map<ResourceLocation, WrappedVertexBuffer> uploadedBuffers;

    public ModelManager() {
        this.uploadedBuffers = new HashMap<>();
    }

    public void upload(ResourceLocation location, WrappedVertexBuffer vertexBuffer) {
        uploadedBuffers.put(location, vertexBuffer);
    }

    public void release(ResourceLocation location) {
        uploadedBuffers.get(location).releaseAll();
    }

    public void releaseAll() {
        for (ResourceLocation location : uploadedBuffers.keySet()) {
            release(location);
        }
    }

    public WrappedVertexBuffer getWrappedVertexBuffer(ResourceLocation location) {
        return uploadedBuffers.get(location);
    }
}
