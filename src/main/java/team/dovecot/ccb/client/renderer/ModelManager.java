package team.dovecot.ccb.client.renderer;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private static ModelManager INSTANCE = null;

    private final Map<ResourceLocation, WrappedVertexBuffer> uploadedBuffers;

    public ModelManager() {
        this.uploadedBuffers = new HashMap<>();
    }

    public static ModelManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ModelManager();

        return INSTANCE;
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
}
