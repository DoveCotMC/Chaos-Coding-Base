package team.dovecot.ccb.client.mixin;

import com.mojang.blaze3d.shaders.Program;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.shader.TransformableShaderLoader;
import team.dovecot.ccb.client.renderer.shader.TransformableShaderPatcher;
import team.dovecot.ccb.client.renderer.shader.TransformableShaderInstance;
import team.dovecot.ccb.common.ChaosBase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Deprecated
    private static final List<String> shaderFields = List.of(
//            "rendertypeEntitySolidShader",
//            "rendertypeEntityCutoutShader",
//            "rendertypeEntityCutoutNoCullShader",
//            "rendertypeEntityCutoutNoCullZOffsetShader",
//            "rendertypeItemEntityTranslucentCullShader",
//            "rendertypeEntityTranslucentCullShader",
//            "rendertypeEntityTranslucentShader",
//            "rendertypeEntityTranslucentEmissiveShader",
//            "rendertypeEntitySmoothCutoutShader",
//            "rendertypeBeaconBeamShader",
//            "rendertypeEntityDecalShader",
//            "rendertypeEntityNoOutlineShader",
//            "rendertypeEntityShadowShader",
//            "rendertypeEntityAlphaShader",
//            "rendertypeEntityGlintShader",
//            "rendertypeEntityGlintDirectShader"
    );

    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;

    @Shadow
    public abstract @Nullable ShaderInstance getShader(@Nullable String string);

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    private void reloadShaders$ccbInjectTransformableShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        TransformableShaderLoader.closeAll();
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

        if (!Renderer.injectVanillaShader) {
            for (String shaderName : TransformableShaderLoader.SHADER_NAMES) {
                ShaderInstance shaderInstance = getShader(shaderName);

                if (!this.shaders.containsKey(shaderName)) {
                    ChaosBase.LOGGER.error("Shader Instance {} is not loaded!", shaderName);
                    continue;
                }

                ShaderInstance patchedShader = null;
                try {
                    patchedShader = new TransformableShaderInstance(
                            new TransformableShaderPatcher.ResourceProvider(resourceProvider),
                            shaderName + Renderer.patchedShaderSuffix,
                            shaderInstance.getVertexFormat()
                    );
                } catch (IOException e) {
                    ChaosBase.LOGGER.error("Could not patch shader: {}!", shaderName);
                    throw new RuntimeException(e);
                }

                TransformableShaderLoader.PATCHED_SHADERS.put(shaderName, patchedShader);
                ChaosBase.LOGGER.info("Patched Shader loaded: {}", shaderName);
            }
        } else {
            for (String shaderName : TransformableShaderLoader.SHADER_NAMES) {
                Field[] fields = GameRenderer.class.getDeclaredFields();
                for (Field shaderField : fields) {
                    // Shader fields are static
                    if (!Modifier.isStatic(shaderField.getModifiers()))
                        continue;

                    if (shaderField.getType().equals(ShaderInstance.class)) {
                        if (!shaderField.trySetAccessible()) {
                            ChaosBase.LOGGER.error("Unable to access shader {} field: {}!", shaderName, shaderField.getName());
                            continue;
                        }

                        ShaderInstance shaderInstance;
                        try {
                            shaderInstance = (ShaderInstance) shaderField.get(null);

                            if (shaderInstance != null) {
                                if (!shaderInstance.getClass().equals(ShaderInstance.class)) {
                                    ChaosBase.LOGGER.warn("Shader {} has been modified by another mod. Patcher will skip this shader.", shaderInstance.getName());
                                    return;
                                }
                            }
                        } catch (IllegalAccessException e) {
                            ChaosBase.LOGGER.error("Unable to get shader {} from field: {}!", shaderName, shaderField.getName());
                            throw new RuntimeException(e);
                        }

                        if (!shaderInstance.getName().equals(shaderName))
                            continue;

                        if (!this.shaders.containsKey(shaderName)) {
                            ChaosBase.LOGGER.error("Shader Instance {} is not loaded!", shaderName);
                            continue;
                        }

                        ShaderInstance patchedShader;
                        try {
                            // Remove existing programs
                            if (Program.Type.VERTEX.getPrograms().containsKey(shaderName)) {
                                Program.Type.VERTEX.getPrograms().get(shaderName).close();
                                Program.Type.VERTEX.getPrograms().remove(shaderName);
                            }
                            if (Program.Type.FRAGMENT.getPrograms().containsKey(shaderName)) {
                                Program.Type.FRAGMENT.getPrograms().get(shaderName).close();
                                Program.Type.FRAGMENT.getPrograms().remove(shaderName);
                            }

                            patchedShader = new TransformableShaderInstance(
                                    new TransformableShaderPatcher.ResourceProvider(resourceProvider),
                                    shaderName,
                                    shaderInstance.getVertexFormat()
                            );
                        } catch (IOException e) {
                            ChaosBase.LOGGER.error("Could not patch shader: {}!", shaderName);
                            throw new RuntimeException(e);
                        }

                        try {
                            shaderField.set(null, patchedShader);
                        } catch (IllegalAccessException e) {
                            ChaosBase.LOGGER.error("Could not hijack shader: {}!", shaderName);
                            throw new RuntimeException(e);
                        }

                        this.shaders.get(shaderName).close();
                        this.shaders.remove(shaderName);
                        this.shaders.put(shaderName, patchedShader);
                    }
                }
            }

            // TODO: Deprecated
            for (String fieldName : shaderFields) {
                Field shaderField;
                String resolvedName = resolver.mapClassName("intermediary", fieldName);
                try {
                    shaderField = GameRenderer.class.getDeclaredField(resolver.mapClassName("intermediary", fieldName));
                } catch (NoSuchFieldException e) {
                    ChaosBase.LOGGER.error("Shader field {} (or {} unmapped) not found!", resolvedName, fieldName);
                    throw new RuntimeException(e);
                }

                if (!shaderField.trySetAccessible()) {
                    ChaosBase.LOGGER.error("Unable to access shader field: {} (or {} unmapped)!", resolvedName, fieldName);
                    continue;
                }

                ShaderInstance shaderInstance;
                try {
                    shaderInstance = (ShaderInstance) shaderField.get(null);

                    if (shaderInstance != null) {
                        if (!shaderInstance.getClass().equals(ShaderInstance.class)) {
                            ChaosBase.LOGGER.warn("Shader {} has been modified by another mod. Patcher will skip this shader.", shaderInstance.getName());
                            return;
                        }
                    }
                } catch (IllegalAccessException e) {
                    ChaosBase.LOGGER.error("Unable to get shader from field: {} (or {} unmapped)!", resolvedName, fieldName);
                    throw new RuntimeException(e);
                }

                String shaderName = shaderInstance.getName();

                if (!this.shaders.containsKey(shaderName)) {
                    ChaosBase.LOGGER.error("Shader Instance {} is not loaded!", shaderName);
                    continue;
                }

                ShaderInstance patchedShader = null;
                try {
                    // Remove existing programs
                    if (Program.Type.VERTEX.getPrograms().containsKey(shaderName)) {
                        Program.Type.VERTEX.getPrograms().get(shaderName).close();
                        Program.Type.VERTEX.getPrograms().remove(shaderName);
                    }
                    if (Program.Type.FRAGMENT.getPrograms().containsKey(shaderName)) {
                        Program.Type.FRAGMENT.getPrograms().get(shaderName).close();
                        Program.Type.FRAGMENT.getPrograms().remove(shaderName);
                    }

                    patchedShader = new TransformableShaderInstance(
                            new TransformableShaderPatcher.ResourceProvider(resourceProvider),
                            shaderName,
                            shaderInstance.getVertexFormat()
                    );
                } catch (IOException e) {
                    ChaosBase.LOGGER.error("Could not patch shader: {}!", shaderName);
                    throw new RuntimeException(e);
                }

                try {
                    shaderField.set(null, patchedShader);
                } catch (IllegalAccessException e) {
                    ChaosBase.LOGGER.error("Could not hijack shader: {}!", shaderName);
                    throw new RuntimeException(e);
                }

                this.shaders.get(shaderName).close();
                this.shaders.remove(shaderName);
                this.shaders.put(shaderName, patchedShader);
            }
        }
    }
}
