package team.dovecot.ccb.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import team.dovecot.ccb.client.mixin.accessor.AccessorVertexBuffer;
import team.dovecot.ccb.client.renderer.model.LocalModel;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WrappedVertexBuffer {
    private final Map<String, VertexBuffer> buffers;
    private final Map<String, ResourceLocation> textures;
    private final Map<String, WrappedVertexBuffer> children;
//    private final Map<String, ModelTransformer> modelTransformers;

    private WrappedVertexBuffer(Map<String, VertexBuffer> buffers, Map<String, ResourceLocation> textures, Map<String, WrappedVertexBuffer> children) {
        this.buffers = buffers;
        this.textures = textures;
        this.children = children;
//        this.modelTransformers = modelTransformers;
    }

    public static WrappedVertexBuffer upload(LocalModel localModel, Matrix4f pose, Matrix3f normal, int light, int overlay) {
        RenderSystem.assertOnRenderThread();

        Map<String, WrappedVertexBuffer> children = new HashMap<>();
        Map<String, VertexBuffer> buffers = new HashMap<>();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        for (Map.Entry<String, List<Face>> entry : localModel.getFaces().entrySet()) {
            String materialName = entry.getKey();
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            localModel.consume(materialName, builder, pose, normal, overlay, light);

            // Finalize and Upload
            BufferBuilder.RenderedBuffer renderedBuffer = builder.end();
            ByteBuffer indexBuffer = renderedBuffer.indexBuffer().duplicate();
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

    public void render(IRenderContext context) {
        for (Map.Entry<String, VertexBuffer> entry : this.buffers.entrySet()) {
            String material = entry.getKey();
            VertexBuffer vertexBuffer = entry.getValue();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, getTexture(material));

            vertexBuffer.bind();

            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutShader);
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

            // The index of Lightmap UV or UV2 is 4
            vertexBuffer.bind();
            GL30.glDisableVertexAttribArray(4);

            int lightProcessed = 0xF00000;
            lightProcessed = context.getLight();

            // Insert Lightmap UV
            GL30.glVertexAttribI2i(4, lightProcessed & 0xFFFF, lightProcessed >> 16 & 0xFFFF);

            ShaderInstance shaderInstance = RenderSystem.getShader();

            if (shaderInstance == null)
                return;

//            Renderer.setupShader(shaderInstance, projectionMatrix, modelViewMatrix, ((AccessorVertexBuffer) vertexBuffer).getMode());
//            shaderInstance.apply();
//            RenderSystem.drawElements(((AccessorVertexBuffer) vertexBuffer).getMode().asGLMode, ((AccessorVertexBuffer) vertexBuffer).getIndexCount(), ((AccessorVertexBuffer) vertexBuffer).getIndexType().asGLType);
//            shaderInstance.clear();

            if (shaderInstance.getUniform(TransformableShaderInstance.TRANSFORM_MAT) != null) {
                Objects.requireNonNull(shaderInstance.getUniform(TransformableShaderInstance.TRANSFORM_MAT)).set(context.getPoseMatrix());
                vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderInstance);
                Objects.requireNonNull(shaderInstance.getUniform(TransformableShaderInstance.TRANSFORM_MAT)).set(new Matrix4f());
            } else {
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
