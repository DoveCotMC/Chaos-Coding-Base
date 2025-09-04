package team.dovecot.ccb.client.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import team.dovecot.ccb.common.block.entity.BlockEntityTest;

public class BlockEntityRendererTest implements BlockEntityRenderer<BlockEntityTest> {
    @Override
    public void render(BlockEntityTest blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        Tesselator tesselator = Tesselator.getInstance();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();

        BufferBuilder builder = tesselator.getBuilder();

        poseStack.pushPose();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(poseStack.last().pose(), 0, 0, 0).color(1f, 0f, 0f, 1f).endVertex();
        builder.vertex(poseStack.last().pose(), 1, 0, 0).color(0f, 1f, 0f, 1f).endVertex();
        builder.vertex(poseStack.last().pose(), 0.5f, 1, 0).color(0f, 0f, 1f, 1f).endVertex();
//        builder.vertex(0, 0, 0).color(1f, 0f, 0f, 1f).endVertex();
//        builder.vertex(1, 0, 0).color(0f, 1f, 0f, 1f).endVertex();
//        builder.vertex(1, 1, 0).color(0f, 0f, 1f, 1f).endVertex();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        tesselator.end();
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 4096;
    }
}
