package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockAdded
{
    @Override
    public void notifyOfFullStagePlantGrowthOn(World world, BlockPos pos, Block plantBlock) {

    }

    /**
     * This is used by old style non-daily plant growth
     */
    public float getPlantGrowthOnMultiplier(World world, BlockPos pos, Block plantBlock) { return 1F; }


    @Override
    public boolean isBlockHydratedForPlantGrowthOn(World world, BlockPos pos)
    {
        return false;
    }
    @Override
    public int getWeedsGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return 0;
    }
    @Override
    public void removeWeeds(World world, BlockPos pos)
    {
    }


}
