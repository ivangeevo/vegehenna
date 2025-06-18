package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;


public class FloweringCarrotBlock extends CarrotsBlock
{

    public FloweringCarrotBlock(Settings settings) {
        super(settings);
    }

    // Method is not actually identical to super since we modify the CarrotsBlock's seed item to be the Carrot Seeds
    @Override
    protected ItemConvertible getSeedsItem() {
        return Items.CARROT;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DailyGrowthCrop.FLOWERING_CARROTS_AGE_TO_SHAPE[this.getAge(state)];
    }

}
