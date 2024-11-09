package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.ivangeevo.vegehenna.block.interfaces.CropBlockAdded;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable, CropBlockAdded
{
    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract int getAge(BlockState state);
    @Shadow public abstract int getMaxAge();

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    // Making all crops to not drop their stacks when broken by being stepped-on
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onSteppedOn(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, false, entity);
        }
        super.onEntityCollision(state, world, pos, entity);

        ci.cancel();
    }

    // Custom outline shape to allow placing of fertilizer on the block below
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( NEW_AGE_TO_SHAPE[this.getAge(state)] );
    }

    // Make it not fertilizable
    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    private void injectedIsFertilizable(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
       cir.setReturnValue(floor.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS) || floor.isOf(Blocks.FARMLAND));
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random, CallbackInfo ci)
    {

        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
        {
            attemptToGrow(world, pos, state, random);
        }

        ci.cancel();

    }

    @Override
    public void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand)
    {

        if (getWeedsGrowthLevel(world, pos) == 0 && getGrowthLevel(world, pos) < this.getMaxAge() && world.getLightLevel( pos.up() ) >= 9 )
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
    public float getBaseGrowthChance()
    {
        return 0.05F;
    }

    @Override
    public int getGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return this.getAge(blockAccess.getBlockState(pos));
    }

    @Unique
    private static final VoxelShape[] NEW_AGE_TO_SHAPE = new VoxelShape[]
            {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 9.0, 14.0)
            };

}
