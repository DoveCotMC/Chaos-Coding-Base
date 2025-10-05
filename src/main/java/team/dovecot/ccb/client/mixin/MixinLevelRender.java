package team.dovecot.ccb.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.tasks.ILevelRenderTask;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRender {
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
        }
    }
}
