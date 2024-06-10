package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SugarCaneBlock extends SugarCaneBlockBase
{
    public SugarCaneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        return blockBelow instanceof SugarCaneBlockBase;
    }
}
