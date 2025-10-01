package team.dovecot.ccb.client.renderer.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import team.dovecot.ccb.client.renderer.ModelManager;
import team.dovecot.ccb.client.renderer.WrappedVertexBuffer;

// bruh this class looks soo useless
public class UploadedModel {
    private final ResourceLocation location;

    private UploadedModel(ResourceLocation location) {
        this.location = location;
    }

    public static UploadedModel upload(LocalModel model) {
        ModelManager.getInstance().upload(model.getLocation(), WrappedVertexBuffer.upload(model, new Matrix4f(), new Matrix3f(), 0, OverlayTexture.NO_OVERLAY));
        return new UploadedModel(model.getLocation());
    }

    public void release() {
        ModelManager.getInstance().release(location);
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
}
