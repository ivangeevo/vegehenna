package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BeetrootsBlock.class)
public abstract class BeetrootsBlockMixin extends CropBlock {


    @Shadow @Final public static IntProperty AGE;
    @Unique
    private static final VoxelShape[] BTWR_AGE_TO_SHAPE = new VoxelShape[]
            {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0)
            };


    public BeetrootsBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( BTWR_AGE_TO_SHAPE[this.getAge(state)] );
    }



    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
        {
            this.attemptToGrow(world, pos, state, random);
        }

        ci.cancel();
    }

    @Override
    public int getAge(BlockState state) { return state.get(this.getAgeProperty()); }

    @Override
    public IntProperty getAgeProperty() { return AGE; }

    @Override
    public void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand)
    {
        if (/** getWeedsGrowthLevel(world, i, j, k) == 0 && **/
                getGrowthLevel(world, pos) < 3 &&
                world.getLightLevel( pos.up() ) >= 9 )
        {
            BlockState belowState = world.getBlockState(pos.down());

            if ( belowState != null && (belowState.getBlock()).isBlockHydratedForPlantGrowthOn(world, pos.down()))
            {
                float fGrowthChance = getBaseGrowthChance() *
                        (belowState.getBlock()).getPlantGrowthOnMultiplier(world, pos.down(), this);

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

        if (iGrowthLevel >= 3)
        {
            BlockState belowState = world.getBlockState(pos.down());

            if ( belowState != null )
            {
                belowState.getBlock().notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }
        }
    }

    @Override
    public float getBaseGrowthChance()
    {
        return 0.05F;
    }

    @Override
    public int getGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return this.getAge(blockAccess.getBlockState(pos));
    }



}
