package team.dovecot.ccb.client.renderer;

import net.minecraft.client.renderer.texture.AbstractTexture;
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<String, AbstractTexture> textures = new HashMap<>();
    private static TextureManager instance = null;

    public static TextureManager getInstance() {
        if (instance == null)
            instance = new TextureManager();

        return instance;
    }

    public void uploadModel(ResourceIdentifier identifier, byte[] bytes) {
    }

    public void releaseAll() {
        textures.forEach((s, abstractTexture) -> {
            abstractTexture.close();
            abstractTexture.releaseId();
        });
    }
}
