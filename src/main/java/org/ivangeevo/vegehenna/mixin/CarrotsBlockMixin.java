package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.ivangeevo.vegehenna.block.interfaces.CarrotsBlockAdded;
import org.ivangeevo.vegehenna.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CarrotsBlock.class)
public abstract class CarrotsBlockMixin extends CropBlock implements CarrotsBlockAdded
{
    // New constants for the AGE property which is now 4 instead of 7;
    @Unique private static final int MAX_AGE = CarrotsBlockAdded.MAX_AGE;
    @Unique private static final IntProperty AGE = CarrotsBlockAdded.AGE;

    public CarrotsBlockMixin(Settings settings) {
        super(settings);
    }




    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedShapes(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( NEW_AGE_TO_SHAPE[this.getAge(state)] );
    }

    @Inject(method = "getSeedsItem", at = @At("HEAD"), cancellable = true)
    private void injectedCustomSeedItem(CallbackInfoReturnable<ItemConvertible> cir)
    {
        cir.setReturnValue( ModItems.CARROT_SEEDS );
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !(this.getAge(state) >= this.getMaxAge());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
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

            if ( blockBelow != null && ((BlockAdded)blockBelow).isBlockHydratedForPlantGrowthOn(world, pos.down()))
            {
                float fGrowthChance = getBaseGrowthChance() *
                        ((BlockAdded) blockBelow).getPlantGrowthOnMultiplier(world, pos.down(), this);

                if ( rand.nextFloat() <= fGrowthChance )
                {
                    incrementGrowthLevel(world, pos, state);
                }
            }
        }

    }

    @Override
    public void incrementGrowthLevel(World world, BlockPos pos, BlockState state)
    {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (getGrowthLevel(world,pos) >= this.getMaxAge())
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null )
            {
                ((BlockAdded)blockBelow).notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Override
    public int getGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return this.getAge(blockAccess.getBlockState(pos));
    }

    @Override
    public float getBaseGrowthChance()
    {
        return 0.04F;
    }


    @Unique
    private static final VoxelShape[] NEW_AGE_TO_SHAPE = new VoxelShape[]
            {
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0)
            };

}
