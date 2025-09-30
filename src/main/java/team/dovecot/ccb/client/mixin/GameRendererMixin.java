package team.dovecot.ccb.client.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.dovecot.ccb.client.renderer.TransformableShaderInstance;
import team.dovecot.ccb.common.ChaosBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    private void ccbInjectTransformableShaders$reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
//        List<String> toRemove = new ArrayList<>();
//        for (String shaderName : this.shaders.keySet()) {
//            if (shaderName.startsWith("rendertype_entity_")) {
//                System.out.println(shaderName);
//                toRemove.add(shaderName);
//            }
//        }
//
//        for (String shaderName : toRemove) {
//            // Load customised shader
//            ShaderInstance patchedShader = null;
//            try {
//                VertexFormat vertexFormat = this.shaders.get(shaderName).getVertexFormat();
//                patchedShader = new TransformableShaderInstance(resourceProvider, shaderName, vertexFormat);
//            } catch (IOException e) {
//                ChaosBase.LOGGER.error("Failed to load patched shader " + shaderName + "!!!");
//                throw new RuntimeException(e);
//            }
//
//            System.out.println("Patching shader complete: " + shaderName);
//
//            // Store
//            this.shaders.get(shaderName).close();
//            this.shaders.remove(shaderName);
//            this.shaders.put(shaderName, patchedShader);
//        }
    }
}
