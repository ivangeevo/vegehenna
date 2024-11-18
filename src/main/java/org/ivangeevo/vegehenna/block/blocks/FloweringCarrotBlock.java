package org.ivangeevo.vegehenna.block.blocks;

import btwr.lib.added.BlockAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Unique;

public class FloweringCarrotBlock extends CropBlock
{
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    public FloweringCarrotBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
        {
            attemptToGrow(world, pos, state, random);
        }
    }
    @Override
    public void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand)
    {

        if (/** getWeedsGrowthLevel(world, i, j, k) == 0 && **/
                getGrowthLevel(world, pos) < 3 &&
                        world.getLightLevel( pos.up() ) >= 9 )
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null && (blockBelow.isBlockHydratedForPlantGrowthOn(world, pos.down())))
            {
                float fGrowthChance = getBaseGrowthChance() *
                        blockBelow.getPlantGrowthOnMultiplier(world, pos.down(), this);

                if ( rand.nextFloat() <= fGrowthChance )
                {
                    incrementGrowthLevel(world, pos, state);
                }
            }
        }

    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        // only allow the block to have random ticks if not fully grown(mature)
        return !(this.getAge(state) >= this.getMaxAge());
    }


    @Unique
    public void incrementGrowthLevel(World world, BlockPos pos, BlockState state)
    {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (getGrowthLevel(world,pos) >= 3)
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null )
            {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Unique
    public int getGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return this.getAge(blockAccess.getBlockState(pos));
    }


    @Override
    public float getBaseGrowthChance()
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return NEW_AGE_TO_SHAPE[this.getAge(state)];
    }

    @Unique
    private static final VoxelShape[] NEW_AGE_TO_SHAPE = new VoxelShape[]
            {
                    Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
                    Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
                    Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 11.0, 13.0),
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)
            };

}
