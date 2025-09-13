package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<ResourceIdentifier, Integer> textures = new HashMap<>();
    private static TextureManager instance = null;

    public static TextureManager getInstance() {
        if (instance == null)
            instance = new TextureManager();

        return instance;
    }

    public void uploadModel(ResourceIdentifier identifier, byte[] bytes) {
        try {
            NativeImage nativeImage = NativeImage.read(bytes);
            int textureId = TextureUtil.generateTextureId();
            TextureUtil.prepareImage(textureId, 0, nativeImage.getWidth(), nativeImage.getHeight());
            nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, false, false, true);
            textures.put(identifier, textureId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bindTexture(int texture, ResourceIdentifier identifier) {
        RenderSystem.setShaderTexture(texture, textures.get(identifier));
    }

    public void releaseAll() {
        textures.forEach((s, textureId) -> {
            TextureUtil.releaseTextureId(textureId);
        });
    }
}
