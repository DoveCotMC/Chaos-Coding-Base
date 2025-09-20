package team.dovecot.ccb.client.renderer;

import net.minecraft.resources.ResourceLocation;
import team.dovecot.ccb.client.renderer.model.UploadedModel;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private static ModelManager INSTANCE = null;

    private final Map<ResourceLocation, UploadedModel> uploadedModels;

    public ModelManager() {
        this.uploadedModels = new HashMap<>();
    }

    public static ModelManager getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new ModelManager();

        return INSTANCE;
    }
}
