package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;

public interface SugarCaneBlockBase
{
    IntProperty AGE = Properties.AGE_15;
    double WIDTH = 0.75D;
    double HALF_WIDTH = WIDTH / 2;

    default void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    default void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.canPlaceAt(world, pos))
        {
            if (world.isAir(pos.up()))
            {
                int reedHeight = 1;
                BlockPos posAtBase = pos.down();

                // Calculate reed height
                while (reedHeight < 3 && world.getBlockState(posAtBase).getBlock() instanceof SugarCaneBlockBase)
                {
                    reedHeight++;
                    posAtBase = posAtBase.down(); // Move one block down each iteration
                }

                // If reed height is less than 3, try to grow the sugarcane
                if (reedHeight < 3)
                {
                    int age = state.get(AGE);
                    if (age == 15)
                    {
                        world.setBlockState(pos.up(), Blocks.SUGAR_CANE.getDefaultState());
                        world.setBlockState(pos, state.with(AGE, 0), 4);
                    }
                    else
                    {
                        world.setBlockState(pos, state.with(AGE, age + 1), 4);
                    }
                }
            }
        }
    }

    /**
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    **/

    // TODO: Fix to work with all blocks that are considered moist, instead of only around the block like this.
    //  We need to check below water cases for BWT Planter blocks
    default boolean isConsideredNeighbouringWaterForReedGrowthOn(WorldView world, BlockPos pos) {
        for (int i = pos.getX() - 1; i <= pos.getX() + 1; i++) {
            for (int j = pos.getZ() - 1; j <= pos.getZ() + 1; j++) {
                BlockPos tempPos = new BlockPos(i, pos.getY(), j);
                if (world.getBlockState(tempPos).getBlock() == Blocks.WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    default void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }
}
