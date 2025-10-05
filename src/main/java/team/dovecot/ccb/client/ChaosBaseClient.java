package team.dovecot.ccb.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import team.dovecot.ccb.client.block.entity.BlockEntityRendererTest;
import team.dovecot.ccb.client.renderer.Renderer;
import team.dovecot.ccb.client.renderer.model.LocalModel;
import team.dovecot.ccb.client.renderer.model.ObjLoader;
import team.dovecot.ccb.client.renderer.model.UploadedModel;
import team.dovecot.ccb.common.ChaosBase;
import team.dovecot.ccb.common.block.CCBBlocks;
import team.dovecot.ccb.common.file.VanillaAssetsFileProvider;

public class ChaosBaseClient implements ClientModInitializer {
    public static LocalModel testModel = null;
    public static UploadedModel testModelUploaded = null;

    @Override
    public void onInitializeClient() {
        if (ChaosBase.loadDevelopmentContent) {
            BlockEntityRendererRegistryImpl.register(CCBBlocks.CCBBlockEntities.TEST_BLOCK_ENTITY, context -> new BlockEntityRendererTest());
        }

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
