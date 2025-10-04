package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import team.dovecot.ccb.client.renderer.IRenderContext;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.buffer.WrappedVertexBuffer;

// bruh this class looks soo useless
public class UploadedModel {
    private final ResourceLocation location;

    private UploadedModel(ResourceLocation location) {
        this.location = location;
    }

    public static UploadedModel upload(LocalModel model) {
        Renderer.getModelManager().upload(model.getLocation(), WrappedVertexBuffer.upload(model, new Matrix4f(), new Matrix3f(), 0, OverlayTexture.NO_OVERLAY));
        return new UploadedModel(model.getLocation());
    }

    public void release() {
        Renderer.getModelManager().release(location);
    }

    public void drawWithShader() {
        this.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    public void drawWithShader(ShaderInstance shaderInstance) {
        this.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderInstance);
    }

    public void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shaderInstance) {
//        this.buffer.drawWithShader(modelViewMatrix, projectionMatrix, shaderInstance);
    }

    public void renderAll(IRenderContext context) {
        Renderer.getModelManager().getWrappedVertexBuffer(this.location).renderAll(context);
    }
}
