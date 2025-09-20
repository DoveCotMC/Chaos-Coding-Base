package team.dovecot.ccb.client.block.entity;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import team.dovecot.ccb.client.ChaosBaseClient;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;
import team.dovecot.ccb.client.renderer.model.UploadedModel;
import team.dovecot.ccb.common.ChaosBase;
import team.dovecot.ccb.common.block.entity.BlockEntityTest;

public class BlockEntityRendererTest implements BlockEntityRenderer<BlockEntityTest> {
//    public static final SimpleTexture simpleTexture = new SimpleTexture(new ResourceLocation("ccb", "model/teapot/teapot.png"));

    @Override
    public void render(BlockEntityTest blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        Tesselator tesselator = Tesselator.getInstance();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();

        // TODO: I need a new model format ToT
        UploadedModel model = Renderer.getInstance().getModel(new ResourceIdentifier(ChaosBase.MOD_ID, "utah_teapot"));
        if (model != null) {
            model.drawWithShader();
        }

        BufferBuilder builder = tesselator.getBuilder();

//        NativeImage nativeImage = NativeImage.read();
//        Minecraft.getInstance().getTextureManager().preload()

        poseStack.pushPose();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX);
        builder.vertex(poseStack.last().pose(), 0, 0, 0).color(1f, 1f, 1f, 1f).uv(0f, 0f).endVertex();
        builder.vertex(poseStack.last().pose(), 1, 0, 0).color(1f, 1f, 1f, 1f).uv(1f, 0f).endVertex();
        builder.vertex(poseStack.last().pose(), 0.5f, 1, 0).color(1f, 1f, 1f, 1f).uv(0.5f, 1f).endVertex();
//        builder.vertex(0, 0, 0).color(1f, 0f, 0f, 1f).endVertex();
//        builder.vertex(1, 0, 0).color(0f, 1f, 0f, 1f).endVertex();
//        builder.vertex(1, 1, 0).color(0f, 0f, 1f, 1f).endVertex();
        RenderSystem.setShaderTexture(0, ChaosBaseClient.testModel.children.get("cylinder").texture.values().stream().toList().get(0));
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        tesselator.end();
        poseStack.popPose();
    }
}
