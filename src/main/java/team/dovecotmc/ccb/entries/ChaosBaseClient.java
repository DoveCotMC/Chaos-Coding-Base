package team.dovecotmc.ccb.entries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import team.dovecotmc.ccb.client.block.entity.BlockEntityRendererTest;
import team.dovecotmc.ccb.client.renderer.Renderer;
import team.dovecotmc.ccb.client.renderer.model.LocalModel;
import team.dovecotmc.ccb.client.renderer.model.ObjLoader;
import team.dovecotmc.ccb.client.renderer.model.UploadedModel;
import team.dovecotmc.ccb.common.block.CCBBlocks;
import team.dovecotmc.ccb.common.file.VanillaAssetsFileProvider;

public class ChaosBaseClient implements ClientModInitializer {
    public static LocalModel testModel = null;
    public static UploadedModel testModelUploaded = null;

    @Override
    public void onInitializeClient() {
        if (ChaosBase.loadDevelopmentContent) {
            BlockEntityRendererRegistryImpl.register(CCBBlocks.CCBBlockEntities.TEST_BLOCK_ENTITY, context -> new BlockEntityRendererTest());
        }

        // Test
        Renderer.registerLevelRenderTask(new ResourceLocation("ccb", "test"), (poseStack, camera, gameRenderer, lightTexture) -> {
            poseStack.pushPose();
            Renderer.renderModel(testModelUploaded, () -> poseStack.last().pose());
            poseStack.popPose();
        });

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(ChaosBase.MOD_ID, "custom_resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                try {
                    Renderer.reload();
                    // Obj Loader TEST!!!
                    testModel = ObjLoader.load(new ResourceLocation(ChaosBase.MOD_ID, "model/obj/utah_teapot"), "model/teapot/teapot.obj", new VanillaAssetsFileProvider(ChaosBase.MOD_ID, resourceManager));
                    if (testModel != null) {
                        testModelUploaded = UploadedModel.upload(testModel);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
