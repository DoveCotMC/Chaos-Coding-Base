package team.dovecot.ccb.client.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import team.dovecot.ccb.client.ChaosBaseClient;
import team.dovecot.ccb.client.renderer.IRenderContext;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.WrappedVertexBuffer;
import team.dovecot.ccb.common.block.entity.BlockEntityTest;

public class BlockEntityRendererTest implements BlockEntityRenderer<BlockEntityTest> {
    @Override
    public void render(BlockEntityTest blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();

        poseStack.pushPose();
        poseStack.mulPoseMatrix(new Matrix4f().translate(new Vector3f(0.5f, 0.0f, 0.5f)));
        WrappedVertexBuffer buffer;
        if (Renderer.gpuAcceleration) {
            buffer = WrappedVertexBuffer.upload(ChaosBaseClient.testModel, new Matrix4f(), new Matrix3f(), 0, OverlayTexture.NO_OVERLAY);
        } else {
            buffer = WrappedVertexBuffer.upload(ChaosBaseClient.testModel, poseStack.last().pose(), poseStack.last().normal(), light, overlay);
        }

        buffer.renderAll(new IRenderContext() {
            @Override
            public Matrix4f getPoseMatrix() {
                return poseStack.last().pose();
            }

            @Override
            public Matrix3f getNormalMatrix() {
                return poseStack.last().normal();
            }

            @Override
            public int getLight() {
                return light;
            }

            @Override
            public int getOverlay() {
                return overlay;
            }
        });
        buffer.releaseAll();
        poseStack.popPose();
    }
}
