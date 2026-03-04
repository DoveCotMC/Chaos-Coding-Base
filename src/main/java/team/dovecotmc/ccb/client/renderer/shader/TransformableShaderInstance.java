package team.dovecotmc.ccb.client.renderer.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class TransformableShaderInstance extends ShaderInstance {
    public static final String TRANSFORM_MAT = "TransformMat";

    public TransformableShaderInstance(ResourceProvider resourceProvider, String string, VertexFormat vertexFormat) throws IOException {
        super(resourceProvider, string, vertexFormat);
    }
}
