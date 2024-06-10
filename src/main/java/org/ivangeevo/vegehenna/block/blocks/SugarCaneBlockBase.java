package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.tag.ModTags;

public abstract class SugarCaneBlockBase extends Block {
    private static final IntProperty AGE = Properties.AGE_15;
    private static final double WIDTH = 0.75D;
    private static final double HALF_WIDTH = WIDTH / 2;

    public SugarCaneBlockBase(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.getDimensionKey() != DimensionTypes.THE_END && this.canPlaceAt(state, world, pos))
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
                        world.setBlockState(pos.up(), ModBlocks.SUGAR_CANE.getDefaultState());
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

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
            return false;
        }

        BlockState stateBelow = world.getBlockState(pos.down());
        Block blockBelow = stateBelow.getBlock();

        return blockBelow == this.asBlock() || (blockBelow != null && stateBelow.isIn(ModTags.Blocks.REEDS_CAN_PLANT_ON) &&
                isConsideredNeighbouringWaterForReedGrowthOn(world, pos.down()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean isConsideredNeighbouringWaterForReedGrowthOn(WorldView world, BlockPos pos) {
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

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }
}
