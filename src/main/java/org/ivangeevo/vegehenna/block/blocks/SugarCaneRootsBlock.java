package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SugarCaneRootsBlock extends SugarCaneBlockBase
{
    public SugarCaneRootsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        return blockBelow != null &&
                !(blockBelow instanceof SugarCaneBlock) &&
                super.canPlaceAt(state, world, pos) && blockBelow != this;
    }


}
