package team.dovecot.ccb.client.renderer;

import org.jetbrains.annotations.Nullable;
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;
import team.dovecot.ccb.client.renderer.model.UploadedModel;

import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private static Renderer instance = null;

    private final Map<ResourceIdentifier, UploadedModel> loadedModels = new HashMap<>();

    private Renderer() {
    }

    public static Renderer getInstance() {
        if (instance == null)
            instance = new Renderer();

        return instance;
    }

    @Nullable
    public UploadedModel getModel(ResourceIdentifier identifier) {
        return loadedModels.get(identifier);
    }

    private void releaseResources() {
        for (UploadedModel model : loadedModels.values()) {
            model.release();
        }
    }

    public static void close() {
        instance.releaseResources();
        instance = null;
    }
}
