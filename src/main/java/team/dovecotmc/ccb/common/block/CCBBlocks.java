package team.dovecotmc.ccb.common.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import team.dovecotmc.ccb.common.ChaosBase;
import team.dovecotmc.ccb.common.block.entity.BlockEntityTest;

public class CCBBlocks {
    public static final Block TEST_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(ChaosBase.MOD_ID, "test_block"), new BlockTest(BlockBehaviour.Properties.of()));

    public static void init() {
        CCBBlockEntities.init();
    }

    public static class CCBBlockEntities {
        public static final BlockEntityType<BlockEntityTest> TEST_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(ChaosBase.MOD_ID, "test_block"), FabricBlockEntityTypeBuilder.create(BlockEntityTest::new, CCBBlocks.TEST_BLOCK).build());

        public static void init() {
        }
    }
}
