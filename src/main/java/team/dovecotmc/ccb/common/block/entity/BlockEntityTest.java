package team.dovecotmc.ccb.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.dovecotmc.ccb.common.block.CCBBlocks;

public class BlockEntityTest extends BlockEntity {
    public BlockEntityTest(BlockPos blockPos, BlockState blockState) {
        super(CCBBlocks.CCBBlockEntities.TEST_BLOCK_ENTITY, blockPos, blockState);
    }
}
