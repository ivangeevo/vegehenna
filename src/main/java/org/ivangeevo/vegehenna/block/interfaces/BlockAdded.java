package org.ivangeevo.vegehenna.block.interfaces;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface BlockAdded
{

    /**
     * Called when a plant hits a full growth stage, like wheat fully grown,
     * or each full block of Hemp.  Used to clear fertilizer.
     */
    void notifyOfFullStagePlantGrowthOn(World world, BlockPos pos, Block plantBlock);

    /**
     * This is used by old style non-daily plant growth
     */
    float getPlantGrowthOnMultiplier(World world, BlockPos pos, Block plantBlock);

    boolean isBlockHydratedForPlantGrowthOn(World world, BlockPos pos);

    /**
     * The growth level of weeds growing out of this block.  Range of 0 to 7
     */
     int getWeedsGrowthLevel(WorldAccess blockAccess, BlockPos pos);

     void removeWeeds(World world, BlockPos pos);

}
