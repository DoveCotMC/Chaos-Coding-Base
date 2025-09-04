package team.dovecot.ccb.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import team.dovecot.ccb.client.block.entity.BlockEntityRendererTest;
import team.dovecot.ccb.common.block.CCBBlocks;

public class ChaosBaseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockEntityRendererRegistryImpl.register(CCBBlocks.CCBBlockEntities.TEST_BLOCK_ENTITY, context -> new BlockEntityRendererTest());
        }
    }
}
