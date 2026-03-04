package team.dovecotmc.ccb.client.renderer.buffer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import team.dovecotmc.ccb.client.renderer.IRenderContext;
import team.dovecotmc.ccb.client.renderer.Renderer;
import team.dovecotmc.ccb.client.renderer.shader.TransformableShaderInstance;
import team.dovecotmc.ccb.client.renderer.model.LocalModel;
import team.dovecotmc.ccb.client.renderer.model.record.Face;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedVertexBuffer {
    private final Map<String, VertexBuffer> buffers;
    private final Map<String, ResourceLocation> textures;
    private final Map<String, WrappedVertexBuffer> children;

    public WrappedVertexBuffer() {
        this.buffers = new HashMap<>();
        this.textures = new HashMap<>();
        this.children = new HashMap<>();
    }

    private WrappedVertexBuffer(Map<String, VertexBuffer> buffers, Map<String, ResourceLocation> textures, Map<String, WrappedVertexBuffer> children) {
        this.buffers = buffers;
        this.textures = textures;
        this.children = children;
    }

    public static WrappedVertexBuffer upload(LocalModel localModel, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        RenderSystem.assertOnRenderThread();

        Map<String, WrappedVertexBuffer> children = new HashMap<>();
        Map<String, VertexBuffer> buffers = new HashMap<>();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        for (Map.Entry<String, List<Face>> entry : localModel.getFaces().entrySet()) {
            String materialName = entry.getKey();
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            localModel.consume(materialName, builder, pose, normal, light, overlay);

            // Finalize and Upload
            BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            vertexBuffer.bind();
            vertexBuffer.upload(renderedBuffer);
            buffers.put(materialName, vertexBuffer);
        }

        for (Map.Entry<String, LocalModel> entry : localModel.getChildren().entrySet()) {
            String name = entry.getKey();
            LocalModel child = entry.getValue();
            children.put(name, upload(child, pose, normal, light, overlay));
        }

        return new WrappedVertexBuffer(buffers, localModel.getTexture(), children);
    }

    public WrappedVertexBuffer uploadAll(LocalModel localModel, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        RenderSystem.assertOnRenderThread();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        for (Map.Entry<String, List<Face>> entry : localModel.getFaces().entrySet()) {
            String materialName = entry.getKey();
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            localModel.consume(materialName, builder, pose, normal, light, overlay);

            // Finalize and Upload
            BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            vertexBuffer.bind();
            vertexBuffer.upload(renderedBuffer);
            buffers.put(materialName, vertexBuffer);
        }

        for (Map.Entry<String, LocalModel> entry : localModel.getChildren().entrySet()) {
            String name = entry.getKey();
            LocalModel child = entry.getValue();
            children.put(name, uploadAll(child, pose, normal, light, overlay));
        }
        this.textures.putAll(localModel.getTexture());

        return this;
    }

    public WrappedVertexBuffer uploadMaterial(LocalModel localModel, String materialName, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        RenderSystem.assertOnRenderThread();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        localModel.consume(materialName, builder, pose, normal, light, overlay);

        // Finalize and Upload
        BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        vertexBuffer.bind();
        vertexBuffer.upload(renderedBuffer);
        buffers.put(materialName, vertexBuffer);
        textures.put(materialName, localModel.getTexture().get(materialName));

        return this;
    }

    public void render(IRenderContext context) {
        for (Map.Entry<String, VertexBuffer> entry : this.buffers.entrySet()) {
            String material = entry.getKey();
            VertexBuffer vertexBuffer = entry.getValue();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, getTexture(material));

            vertexBuffer.bind();

            Renderer.setShader(GameRenderer::getRendertypeEntityCutoutShader);
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

            // The index of Lightmap UV or UV2 is 4
            vertexBuffer.bind();

            // Light
            GL30.glDisableVertexAttribArray(4);

            int lightProcessed = context.getLight();

            // Insert Lightmap UV
            GL30.glVertexAttribI2i(4, lightProcessed & 0xFFFF, lightProcessed >> 16 & 0xFFFF);

            ShaderInstance shaderInstance = RenderSystem.getShader();

            if (shaderInstance == null)
                throw new RuntimeException("Shader is null!!!");

//            Renderer.setupShader(shaderInstance, projectionMatrix, modelViewMatrix, ((AccessorVertexBuffer) vertexBuffer).getMode());
//            shaderInstance.apply();
//            RenderSystem.drawElements(((AccessorVertexBuffer) vertexBuffer).getMode().asGLMode, ((AccessorVertexBuffer) vertexBuffer).getIndexCount(), ((AccessorVertexBuffer) vertexBuffer).getIndexType().asGLType);
//            shaderInstance.clear();

            Uniform transformMat = shaderInstance.getUniform(TransformableShaderInstance.TRANSFORM_MAT);
            if (transformMat != null) {
                transformMat.set(context.getPoseMatrix());
                vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderInstance);
                transformMat.set(new Matrix4f());
            } else {
                // Compatibility mode, position will not be perfectly transformed.
                // Example: When shader is enabled.
                vertexBuffer.drawWithShader(new Matrix4f(RenderSystem.getModelViewMatrix()).mul(context.getPoseMatrix()), RenderSystem.getProjectionMatrix(), shaderInstance);
            }

            GL30.glEnableVertexAttribArray(4);
        }
    }

    public void renderAll(IRenderContext context) {
        this.render(context);
        for (WrappedVertexBuffer buffer : this.children.values()) {
            buffer.renderAll(context);
        }
    }

    private void release() {
        for (VertexBuffer buffer : this.buffers.values()) {
            buffer.close();
        }
    }

    public void releaseAll() {
        this.release();
        for (WrappedVertexBuffer child : this.children.values()) {
            child.release();
        }
    }

    public ResourceLocation getTexture(String material) {
        return this.textures.get(material) == null ? MissingTextureAtlasSprite.getLocation() : this.textures.get(material);
    }
}
