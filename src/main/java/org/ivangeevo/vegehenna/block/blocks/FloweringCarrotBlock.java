package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Unique;


public class FloweringCarrotBlock extends CarrotsBlock
{
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    public FloweringCarrotBlock(Settings settings) {
        super(settings);
        //this.setDefaultState(this.stateManager.getDefaultState().with(DailyGrowthCrop.HAS_GROWN_TODAY, false));
    }

    @Unique
    public void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state)
    {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (this.getAge(state) >= 3)
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null )
            {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Override
    public float vegehenna$getBaseGrowthChance()
    {
        return 0.04F;
    }

    @Override
    public int getMaxAge() { return MAX_AGE; }

    @Override
    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return Items.CARROT;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return NEW_AGE_TO_SHAPE[this.getAge(state)];
    }

    @Unique
    private static final VoxelShape[] NEW_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
            Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 11.0, 13.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)
    };

}
