package org.ivangeevo.vegehenna.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.tag.ModTags;

public class SugarCaneHelper {

    public static final IntProperty AGE = Properties.AGE_15;
    public static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    public static final int MAX_HEIGHT = 3;

    // Adds AGE property to a block's state manager
    public static void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // Handles random growth tick logic
    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos) {
        if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && canPlaceAt(world, pos)) {
            if (world.isAir(pos.up())) {
                int reedHeight = 1;
                BlockPos posAtBase = pos.down();

                // Calculate whole reed plant height
                while (reedHeight < MAX_HEIGHT && SugarCaneHelper.isSugarCaneTypeBlock(world.getBlockState(posAtBase).getBlock())) {
                    reedHeight++;
                    posAtBase = posAtBase.down(); // Move one block down each iteration
                }

                // If reed height is less than MAX_HEIGHT, try to grow the sugarcane
                if (reedHeight < MAX_HEIGHT) {
                    int age = state.get(AGE);
                    if (age == 15) {
                        world.setBlockState(pos.up(), Blocks.SUGAR_CANE.getDefaultState());
                        world.setBlockState(pos, state.with(AGE, 0), 4);
                    } else {
                        world.setBlockState(pos, state.with(AGE, age + 1), 4);
                    }
                }
            }
        }
    }

    // Checks if the block can grow based on neighboring water
    public static boolean isConsideredNeighbouringWaterForReedGrowthOn(WorldView world, BlockPos pos) {
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

    // Handles scheduled tick logic
    public static void scheduledTick(ServerWorld world, BlockPos pos) {
        if (!canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    // Determines if the block can be placed
    public static boolean canPlaceAt(WorldView world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return isSugarCaneTypeBlock(blockBelow) || blockBelow.getDefaultState().isIn(ModTags.Blocks.REEDS_CAN_PLANT_ON)
                || blockBelow == Blocks.SAND
                || blockBelow == Blocks.GRAVEL
                || blockBelow == Blocks.GRASS_BLOCK;
    }

     public static boolean isSugarCaneTypeBlock(Block block) {
     return block.getDefaultState().isOf(ModBlocks.SUGAR_CANE_ROOTS)
             || block.getDefaultState().isOf(Blocks.SUGAR_CANE);
     }
}
