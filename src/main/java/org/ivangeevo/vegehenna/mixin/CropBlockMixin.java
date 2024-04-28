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
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.ivangeevo.vegehenna.tag.ModTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.CropBlock.AGE;
import static net.minecraft.block.FarmlandBlock.MOISTURE;
import static org.ivangeevo.vegehenna.state.ModProperties.FERTILIZED;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable {

    @Shadow @Final public static IntProperty AGE;

    @Unique
    private static final VoxelShape[] BTWR_AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 9.0, 14.0)};

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( BTWR_AGE_TO_SHAPE[this.getAge(state)] );
    }

    @Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
       cir.setReturnValue(floor.isIn(ModTags.Blocks.FERTILIZED_FARMLAND_BLOCKS));
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random, CallbackInfo ci) {

        if (world.getDimensionKey() != DimensionTypes.THE_END && state.isOf(this))
        {
            attemptToGrow(world, pos, state, random);
        }
        ci.cancel();
    }

    protected void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand)
    {
        if (/** getWeedsGrowthLevel(world, i, j, k) == 0 && **/
                getGrowthLevel(world, pos) < 7 &&
                world.getLightLevel( pos.up() ) >= 9 )
        {
            BlockState belowState = world.getBlockState(pos.down());

            if ( belowState != null && belowState.get(MOISTURE) == 7)
            {
                float fGrowthChance = getBaseGrowthChance() *
                        ((BlockAdded) belowState.getBlock()).getPlantGrowthOnMultiplier(world, pos.down(), this);

                if ( rand.nextFloat() <= fGrowthChance )
                {
                    incrementGrowthLevel(world, pos, state);
                }
            }
        }
    }

    protected void incrementGrowthLevel(World world, BlockPos pos, BlockState state)
    {
        int iGrowthLevel = getGrowthLevel(world, pos) + 1;

        setGrowthLevel(world, pos, state, iGrowthLevel);

        if (state.get(AGE) >= 7)
        {
            BlockState belowState = world.getBlockState(pos.down());

            if ( belowState != null )
            {
                ((BlockAdded)belowState.getBlock()).notifyOfFullStagePlantGrowthOn(world, pos, this);
            }
        }
    }

    protected void setGrowthLevel(World world, BlockPos pos, BlockState state, int growthLevel) {
        BlockState newState = state.with(AGE, growthLevel);
        world.setBlockState(pos, newState);
    }

    private float getBaseGrowthChance()
    {
        return 0.05F;
    }

    @Unique
    protected int getGrowthLevel(WorldAccess blockAccess, BlockPos pos)
    {
        return this.getAge(blockAccess.getBlockState(pos));
    }




    // Make it not fertilizable
    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    private void injectedIsFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Shadow public abstract int getMaxAge();

    @Shadow public abstract int getAge(BlockState state);

    @Shadow public abstract BlockState withAge(int age);

    @Shadow
    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        boolean bl2;
        float f = 1.0f;
        BlockPos blockPos = pos.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.isOf(Blocks.FARMLAND)) {
                    g = 1.0f;
                    if (blockState.get(MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
        boolean bl3 = bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl32;
            boolean bl4 = bl32 = world.getBlockState(blockPos4.north()).isOf(block) || world.getBlockState(blockPos5.north()).isOf(block) || world.getBlockState(blockPos5.south()).isOf(block) || world.getBlockState(blockPos4.south()).isOf(block);
            if (bl32) {
                f /= 2.0f;
            }
        }
        return f;
    }


}
