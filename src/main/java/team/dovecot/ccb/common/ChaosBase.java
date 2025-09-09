package team.dovecot.ccb.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import team.dovecot.ccb.common.block.CCBBlocks;

public class ChaosBase implements ModInitializer {
    public static final String MOD_ID = "ccb";
    public static final Logger LOGGER = LogManager.getLogger("Chaos Coding Base");

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CCBBlocks.init();
            Configurator.setLevel(LOGGER, Level.ALL);
        }
    }
}
