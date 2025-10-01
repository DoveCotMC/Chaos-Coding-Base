package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import team.dovecot.ccb.client.mixin.accessor.AccessorVertexBuffer;
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;
import team.dovecot.ccb.client.renderer.model.UploadedModel;

import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private static Renderer instance = null;

    public static boolean gpuAcceleration = true;

    private final Map<ResourceIdentifier, UploadedModel> loadedModels = new HashMap<>();

    private Renderer() {
    }

    public static Renderer getInstance() {
        if (instance == null)
            reload();

        return instance;
    }

    public static void reload() {
        if (instance != null) {
            close();
        }
        instance = new Renderer();
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

    public static void setupShader(ShaderInstance shaderInstance, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, VertexFormat.Mode mode) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            shaderInstance.setSampler("Sampler" + i, j);
        }
        if (shaderInstance.PROJECTION_MATRIX != null) {
            shaderInstance.PROJECTION_MATRIX.set(projectionMatrix);
        }
        if (shaderInstance.MODEL_VIEW_MATRIX != null) {
            shaderInstance.MODEL_VIEW_MATRIX.set(modelViewMatrix);
        }
        if (shaderInstance.INVERSE_VIEW_ROTATION_MATRIX != null) {
            shaderInstance.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shaderInstance.COLOR_MODULATOR != null) {
            shaderInstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if (shaderInstance.GLINT_ALPHA != null) {
            shaderInstance.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        }
        if (shaderInstance.FOG_START != null) {
            shaderInstance.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (shaderInstance.FOG_END != null) {
            shaderInstance.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (shaderInstance.FOG_COLOR != null) {
            shaderInstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (shaderInstance.FOG_SHAPE != null) {
            shaderInstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if (shaderInstance.TEXTURE_MATRIX != null) {
            shaderInstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if (shaderInstance.GAME_TIME != null) {
            shaderInstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        if (shaderInstance.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shaderInstance.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
        }
        if (shaderInstance.LINE_WIDTH != null && mode == VertexFormat.Mode.LINES || mode == VertexFormat.Mode.LINE_STRIP) {
            shaderInstance.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(shaderInstance);
    }
}
