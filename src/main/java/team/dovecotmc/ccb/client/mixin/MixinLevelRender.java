package team.dovecotmc.ccb.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.dovecotmc.ccb.client.hitbox.CCBHitResult;
import team.dovecotmc.ccb.client.mixin.accessor.AccessorLevelRenderer;
import team.dovecotmc.ccb.client.renderer.Renderer;
import team.dovecotmc.ccb.client.renderer.tasks.ILevelRenderTask;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRender {
    @Shadow
    public abstract void onResourceManagerReload(ResourceManager resourceManager);

    @Shadow
    protected abstract void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState);

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;",
                    shift = At.Shift.AFTER
            )
    )
    private void renderLevel$ccbRenderTask(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        for (ResourceLocation location : Renderer.getRenderTasks().keySet()) {
            ILevelRenderTask task = Renderer.getRenderTasks().get(location);
            poseStack.pushPose();
            poseStack.mulPoseMatrix(new Matrix4f().translate((float) -camera.getPosition().x(), (float) -camera.getPosition().y(), (float) -camera.getPosition().z()));
            task.render(poseStack, camera, gameRenderer, lightTexture);
            poseStack.popPose();
        }
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V",
                    shift = At.Shift.BY
            )
    )
    private void renderLevel$ccbRenderObbHitOutlines(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        if (Minecraft.getInstance().hitResult instanceof CCBHitResult hitResult) {
            poseStack.pushPose();

            Vector3f reverseCameraPos = camera.getPosition().toVector3f().mul(-1);
            poseStack.translate(reverseCameraPos.x(), reverseCameraPos.y(), reverseCameraPos.z());

//            Matrix4f zFix = new Matrix4f().scaling(-1.0f, 1.0f, -1.0f);
            Matrix4f zFix = new Matrix4f().scaling(1.0f, 1.0f, 1.0f);
            Matrix4f transformMatrix = hitResult.getBoundingBox().getTransformMatrix4f();
            Matrix4f finalMatrix = new Matrix4f();
            zFix.mul(transformMatrix, finalMatrix);

            poseStack.mulPoseMatrix(finalMatrix);

            MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
            VoxelShape unitBox = Shapes.box(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0);
            AccessorLevelRenderer.invokeRenderShape(
                    poseStack,
                    vertexConsumer,
                    unitBox,
                    0.0, 0.0, 0.0,
                    0.0f, 0.0f, 0.0f, 0.4f);


            poseStack.popPose();
        }
    }
}
