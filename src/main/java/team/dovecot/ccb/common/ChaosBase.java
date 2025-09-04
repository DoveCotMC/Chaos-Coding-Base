package team.dovecot.ccb.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import team.dovecot.ccb.common.block.CCBBlocks;

public class ChaosBase implements ModInitializer {
    public static final String MOD_ID = "ccb";

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CCBBlocks.init();
        }
    }
}
