package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FertilizedFarmlandBlock extends FarmlandBlock
{
    public FertilizedFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void notifyOfFullStagePlantGrowthOn(World world, BlockPos pos, Block plantBlock)
    {
        // revert back to unfertilized soil
        world.setBlockState( pos, Blocks.FARMLAND.getDefaultState());
    }

    @Override
    public float getPlantGrowthOnMultiplier(World world, BlockPos pos, Block plantBlock) {
        return 2F;
    }

    @Override
    public boolean isBlockHydratedForPlantGrowthOn(World world, BlockPos pos) {
        return world.getBlockState(pos).get(MOISTURE) == 7;
    }
}
