package team.dovecot.ccb.client.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import team.dovecot.ccb.client.ChaosBaseClient;
import team.dovecot.ccb.client.renderer.IRenderContext;
import team.dovecot.ccb.client.renderer.WrappedVertexBuffer;
import team.dovecot.ccb.common.block.entity.BlockEntityTest;

public class BlockEntityRendererTest implements BlockEntityRenderer<BlockEntityTest> {
    @Override
    public void render(BlockEntityTest blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();

        poseStack.pushPose();
//        poseStack.mulPoseMatrix(new Matrix4f().scale(16, 16, 16));
//        WrappedVertexBuffer buffer = WrappedVertexBuffer.upload(ChaosBaseClient.testModel, poseStack.last().pose(), poseStack.last().normal(), i, j);
        WrappedVertexBuffer buffer = WrappedVertexBuffer.upload(ChaosBaseClient.testModel, new Matrix4f(), new Matrix3f(), i, j);

        buffer.renderAll(new IRenderContext() {
            @Override
            public Matrix4f getPoseMatrix() {
                return poseStack.last().pose();
//                return new Matrix4f();
            }

            @Override
            public Matrix3f getNormalMatrix() {
                return poseStack.last().normal();
//                return new Matrix3f();
            }

            @Override
            public int getLight() {
                return i;
            }

            @Override
            public int getOverlay() {
                return j;
            }
        });
        buffer.releaseAll();
        poseStack.popPose();
    }
}
