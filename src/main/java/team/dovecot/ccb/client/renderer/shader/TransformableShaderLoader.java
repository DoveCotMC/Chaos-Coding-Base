package team.dovecot.ccb.client.renderer.shader;

import net.minecraft.client.renderer.ShaderInstance;

import java.util.HashMap;
import java.util.Map;

public class TransformableShaderLoader {
    public static Map<String, ShaderInstance> patchedShaders = new HashMap<>();

    public static void closeAll() {
        for (ShaderInstance shader : patchedShaders.values()) {
            shader.close();
        }
        patchedShaders.clear();
    }
}
