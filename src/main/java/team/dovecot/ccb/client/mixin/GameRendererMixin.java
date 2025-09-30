package team.dovecot.ccb.client.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
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
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    private static final List<String> shaderFields = List.of(
            "rendertypeEntitySolidShader",
            "rendertypeEntityCutoutShader",
            "rendertypeEntityCutoutNoCullShader",
            "rendertypeEntityCutoutNoCullZOffsetShader",
            "rendertypeItemEntityTranslucentCullShader",
            "rendertypeEntityTranslucentCullShader",
            "rendertypeEntityTranslucentShader",
            "rendertypeEntityTranslucentEmissiveShader",
            "rendertypeEntitySmoothCutoutShader",
            "rendertypeBeaconBeamShader",
            "rendertypeEntityDecalShader",
            "rendertypeEntityNoOutlineShader",
            "rendertypeEntityShadowShader",
            "rendertypeEntityAlphaShader",
            "rendertypeEntityGlintShader",
            "rendertypeEntityGlintDirectShader"
    );

    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    private void ccbInjectTransformableShaders$reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

        for (String fieldName : shaderFields) {
            boolean success = false;
            Field shaderField;
            String resolvedName = resolver.mapClassName("intermediary", fieldName);
            try {
                shaderField = GameRenderer.class.getDeclaredField(resolver.mapClassName("intermediary", fieldName));
            } catch (NoSuchFieldException e) {
                ChaosBase.LOGGER.error("Shader field {} (or {} unmapped) not found!", resolvedName, fieldName);
                continue;
            }

            if (!shaderField.trySetAccessible()) {
                ChaosBase.LOGGER.error("Unable to access shader field: {} (or {} unmapped)!", resolvedName, fieldName);
                continue;
            }

            ShaderInstance shaderInstance;
            try {
                shaderInstance = (ShaderInstance) shaderField.get(null);
            } catch (IllegalAccessException e) {
                ChaosBase.LOGGER.error("Unable to get shader from field: {} (or {} unmapped)!", resolvedName, fieldName);
                continue;
            }

            String shaderName = shaderInstance.getName();

            if (!this.shaders.containsKey(shaderName)) {
                ChaosBase.LOGGER.error("Shader Instance {} is not loaded!", shaderName);
                continue;
            }

            ShaderInstance patchedShader;
            try {
                patchedShader = new TransformableShaderInstance(resourceProvider, shaderName, shaderInstance.getVertexFormat());
            } catch (IOException e) {
                ChaosBase.LOGGER.error("Could not patch shader: {}!", shaderName);
                continue;
            }

            try {
                shaderField.set(null, patchedShader);
            } catch (IllegalAccessException e) {
                ChaosBase.LOGGER.error("Could not hijack shader: {}!", shaderName);
                continue;
            }

            success = patchedShader != null;

            if (success) {
                this.shaders.get(shaderName).close();
                this.shaders.remove(shaderName);
                this.shaders.put(shaderName, patchedShader);
            }
//            System.out.println(shaderName);
//            System.out.println(this.shaders.get(shaderName));
        }

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
