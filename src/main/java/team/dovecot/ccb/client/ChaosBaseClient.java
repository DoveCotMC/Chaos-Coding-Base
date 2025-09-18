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
import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;
import team.dovecot.ccb.client.renderer.model.ObjLoader;
import team.dovecot.ccb.common.ChaosBase;
import team.dovecot.ccb.common.block.CCBBlocks;
import team.dovecot.ccb.common.file.VanillaAssetsFileProvider;

import java.io.IOException;

public class ChaosBaseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockEntityRendererRegistryImpl.register(CCBBlocks.CCBBlockEntities.TEST_BLOCK_ENTITY, context -> new BlockEntityRendererTest());
        }
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("ccb", "custom_resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                try {
                    BlockEntityRendererTest.simpleTexture.releaseId();
                    BlockEntityRendererTest.simpleTexture.load(resourceManager);

                    // Obj Loader TEST!!!
                    ObjLoader.load(new ResourceIdentifier(ChaosBase.MOD_ID, "utah_teapot"), "model/teapot/teapot.obj", new VanillaAssetsFileProvider(ChaosBase.MOD_ID, resourceManager));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // TODO: Interrupt
                System.exit(0);
            }
        });
    }
}
