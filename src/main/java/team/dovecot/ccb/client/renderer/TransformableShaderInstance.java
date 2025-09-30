package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class TransformableShaderInstance extends ShaderInstance {
    public final Uniform TRANSFORM_MAT;

    public TransformableShaderInstance(ResourceProvider resourceProvider, String string, VertexFormat vertexFormat) throws IOException {
        super(resourceProvider, string, vertexFormat);
        TRANSFORM_MAT = this.getUniform("TransformMat");
    }
}
