package org.ivangeevo.vegehenna.block.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface CropBlockAdded {

    boolean vegehenna$requiresNaturalLight();
    float vegehenna$getBaseGrowthChance();
    void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state);
    void vegehenna$attemptToGrow(World world, BlockPos pos, BlockState state, Random rand);
    int vegehenna$getLightLevelForGrowth();

}
