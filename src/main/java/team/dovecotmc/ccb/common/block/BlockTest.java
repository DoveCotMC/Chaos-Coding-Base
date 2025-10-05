package team.dovecotmc.ccb.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.dovecotmc.ccb.common.block.entity.BlockEntityTest;

public class BlockTest extends BaseEntityBlock {
    public BlockTest(Properties properties) {
        super(properties.noCollission().noOcclusion());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntityTest(blockPos, blockState);
    }
}
