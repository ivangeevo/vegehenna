package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends Block
{

    public PlantBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public int getWeedsGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        BlockState state = blockAccess.getBlockState(pos.down());
        Block blockBelow = state.getBlock();

        if ( blockBelow != null && state != blockBelow.getDefaultState() )
        {
            return ((BlockAdded)blockBelow).getWeedsGrowthLevel(blockAccess, pos.down());
        }

        return 0;
    }
    @Override
    public void removeWeeds(World world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState( pos.down()).getBlock();

        if ( blockBelow != null )
        {
            ((BlockAdded)blockBelow).removeWeeds(world, pos.down());
        }
    }
}
