package team.dovecot.ccb.client.renderer.shader;

import net.minecraft.client.renderer.ShaderInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformableShaderLoader {
    public static final List<String> SHADER_NAMES = List.of(
            "rendertype_entity_solid",
            "rendertype_entity_cutout",
            "rendertype_entity_cutout_no_cull",
            "rendertype_entity_cutout_no_cull_z_offset",
            "rendertype_item_entity_translucent_cull",
            "rendertype_entity_translucent_cull",
            "rendertype_entity_translucent",
            "rendertype_entity_translucent_emissive",
            "rendertype_entity_smooth_cutout",
            "rendertype_beacon_beam",
            "rendertype_entity_decal",
            "rendertype_entity_no_outline",
            "rendertype_entity_shadow",
            "rendertype_entity_alpha",
            "rendertype_eyes"
    );

    public static Map<String, ShaderInstance> PATCHED_SHADERS = new HashMap<>();

    public static void closeAll() {
        for (ShaderInstance shader : PATCHED_SHADERS.values()) {
            shader.close();
        }
        PATCHED_SHADERS.clear();
    }
}
