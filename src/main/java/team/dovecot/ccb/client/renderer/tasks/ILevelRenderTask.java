package team.dovecot.ccb.client.renderer.tasks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;

public interface ILevelRenderTask {
    void render(PoseStack poseStack, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture);
}
