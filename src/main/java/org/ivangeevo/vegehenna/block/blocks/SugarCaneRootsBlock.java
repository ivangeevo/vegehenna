package org.ivangeevo.vegehenna.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SugarCaneRootsBlock extends SugarCaneBlockBase
{

    public static final MapCodec<SugarCaneRootsBlock> CODEC = Block.createCodec(SugarCaneRootsBlock::new);

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

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
