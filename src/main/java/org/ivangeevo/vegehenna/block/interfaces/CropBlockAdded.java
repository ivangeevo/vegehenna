package org.ivangeevo.vegehenna.block.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface CropBlockAdded
{
    default float getBaseGrowthChance() { return 0; }

    default void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand) {}

    default void incrementGrowthLevel(World world, BlockPos pos, BlockState state) {}

    default int getGrowthLevel(WorldAccess blockAccess, BlockPos pos) { return 0; }


}
