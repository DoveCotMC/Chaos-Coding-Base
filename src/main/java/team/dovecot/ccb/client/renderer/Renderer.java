package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.dovecot.ccb.client.renderer.buffer.BufferRenderTask;
import team.dovecot.ccb.client.renderer.model.UploadedModel;
import team.dovecot.ccb.client.renderer.shader.TransformableShaderLoader;
import team.dovecot.ccb.client.renderer.tasks.ILevelRenderTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Renderer {
    public static final int LIGHT_FULL_BRIGHT = 0xF000F0;
    public static final int LIGHT_FULL_BRIGHT_BLOCK = 0xF000E0;
    public static final int LIGHT_DEFAULT = 0xF00000;
    private static final Map<ResourceLocation, ILevelRenderTask> LEVEL_RENDER_TASKS = new HashMap<>();

    private static Renderer instance = null;

    public static final boolean injectVanillaShader = false;
    public static String patchedShaderSuffix = ".ccb_patched";

    private final ModelManager modelManager;

    private List<BufferRenderTask> scheduledRender;

    private Renderer() {
        this.modelManager = new ModelManager();
        this.scheduledRender = new ArrayList<>();
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

    public static void close() {
        getModelManager().releaseAll();
        // This should not be here because it might clear loaded shader
//        TransformableShaderLoader.closeAll();
        instance = null;
    }

    public static ModelManager getModelManager() {
        return Renderer.getInstance().modelManager;
    }

    public static void renderModel(UploadedModel model, IRenderContext context) {
        RenderSystem.enableDepthTest();
        model.renderAll(context);
    }

    public static void setShader(Supplier<ShaderInstance> supplier) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> _setShader(supplier.get()));
        } else {
            _setShader(supplier.get());
        }
    }

    private static void _setShader(ShaderInstance shaderInstance) {
        boolean enableCompatibleMode = !shaderInstance.getClass().equals(ShaderInstance.class);

        if (enableCompatibleMode || injectVanillaShader) {
            RenderSystem.setShader(() -> shaderInstance);
        } else {
            RenderSystem.setShader(() -> TransformableShaderLoader.PATCHED_SHADERS.get(shaderInstance.getName()));
        }
    }

    public static void registerLevelRenderTask(ResourceLocation location, ILevelRenderTask task) {
        LEVEL_RENDER_TASKS.put(location, task);
    }

    public static Map<ResourceLocation, ILevelRenderTask> getRenderTasks() {
        return LEVEL_RENDER_TASKS;
    }

    @Deprecated
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
