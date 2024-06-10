package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.tag.ModTags;

public class SugarcaneRootsBlock extends SugarCaneBlock
{
    public SugarcaneRootsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        // prevent growth in the end dimension
        if (world.getDimensionKey() != DimensionTypes.THE_END && this.canPlaceAt(state, world, pos))
        {
            if (world.isAir(pos.up())) {
                int reedHeight = 1;

                BlockPos checkPos = new BlockPos(pos.getX(), pos.getY() - reedHeight, pos.getZ());

                while (world.getBlockState(checkPos).getBlock() instanceof SugarcaneRootsBlock)
                {
                    reedHeight++;
                }

                if (reedHeight < 3)
                {

                    if (state.get(AGE) == 15)
                    {
                        world.setBlockState(pos.up(), Blocks.SUGAR_CANE.getDefaultState());

                        world.setBlockState(pos, state.with(AGE, 0), 4 );
                    }
                    else
                    {
                        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), 4);
                    }

                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        return blockBelow != null &&
                !(blockBelow instanceof SugarCaneBlock) &&
                super.canPlaceAt(state, world, pos);
    }

}
