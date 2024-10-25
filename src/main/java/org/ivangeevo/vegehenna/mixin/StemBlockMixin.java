package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.BlockAdded;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.ivangeevo.vegehenna.util.WorldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// TODO: Make it revert to an earlier AGE when its associated GourdBlock is harvested.
@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends PlantBlock
{
    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract boolean canGrow(World world, Random random, BlockPos pos, BlockState state);

    @Shadow @Final private RegistryKey<Block> gourdBlock;

    @Shadow @Final private RegistryKey<Block> attachedStemBlock;

    public StemBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {


        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
        {
            checkForGrowth(world, pos, state, random);

            validateFruitState(world, pos, state, random);
        }
        ci.cancel();
    }

    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(floor.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS));
    }

    // Make it not fertilizable
    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    private void injectedIsFertilizable(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }


    @Unique
    private void checkForGrowth(World world, BlockPos pos, BlockState state, Random rand)
    {
        if (((BlockAdded)this).getWeedsGrowthLevel(world,pos) == 0 &&
                world.getLightLevel( pos.up() ) >= 9 )
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null &&
                    ((BlockAdded)blockBelow).isBlockHydratedForPlantGrowthOn(world, pos.down()) )
            {
                float fGrowthChance = 0.2F *
                        ((BlockAdded)blockBelow).getPlantGrowthOnMultiplier(world, pos.down(), this);

                if ( rand.nextFloat() <= fGrowthChance )
                {

                    if ( state.get(AGE) < 7 )
                    {
                        world.setBlockState( pos, state.with(AGE, state.get(AGE) + 1) );
                    }
                    else if ( state.get(AGE) == 7  )
                    {
                        int iTargetFacing = 0;

                        if ( hasSpaceToGrow(world, pos, state) )
                        {
                            // if the plant doesn't have space around it to grow,
                            // the fruit will crush its own stem

                            iTargetFacing = rand.nextInt( 4 ) + 2;

                            pos.offset(Direction.byId(iTargetFacing));
                        }

                        if ( canGrowFruitAt(world, pos, state) )
                        {

                            Direction direction = Direction.Type.HORIZONTAL.random(rand);
                            BlockPos blockPos = pos.offset(direction);
                            BlockState blockState = world.getBlockState(blockPos.down());

                            if (world.getBlockState(blockPos).isAir() && ( blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT) ))
                            {
                                Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
                                Optional<Block> optional = registry.getOrEmpty(this.gourdBlock);
                                Optional<Block> optional2 = registry.getOrEmpty(this.attachedStemBlock);

                                if (optional.isPresent() && optional2.isPresent()) {
                                    world.setBlockState(blockPos, optional.get().getDefaultState());
                                    world.setBlockState(pos, optional2.get().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                                }
                                ((BlockAdded)blockBelow).notifyOfFullStagePlantGrowthOn(world, pos.down(), this);

                            }


                        }
                    }
                }
            }
        }
    }


    @Unique
    protected boolean hasSpaceToGrow(World world, BlockPos pos, BlockState state)
    {
        for ( int iTargetFacing = 2; iTargetFacing <= 5; iTargetFacing++ )
        {

            pos.offset(Direction.byId(iTargetFacing));

            if ( canGrowFruitAt(world, pos, state) )
            {
                return true;
            }
        }

        return false;
    }

    protected boolean canGrowFruitAt(World world, BlockPos pos, BlockState state)
    {

        if (WorldUtils.isReplaceableBlock(state) ||
                ( state.getBlock() != null /** &&  state.getBlock() instanceof  **/ &&
                        state != Blocks.COCOA.getDefaultState() ) )
        {
            // CanGrowOnBlock() to allow fruit to grow on tilled earth and such
            if (/** world.isTopSolid( pos.down(), null ) || **/
                    canGrow(world, world.getRandom(), pos.down(), state) )
            {
                return true;
            }
        }

        return false;
    }

    private void validateFruitState(World world, BlockPos pos, BlockState state, Random rand)
    {

        if ( state.get(AGE) == 7 && this.gourdBlock == null && !hasConnectedFruit(world, pos, state))
        {
            // reset to earlier growth stage
            world.setBlockState( pos, state.with(AGE, 4) );
        }
    }

    @Unique
    private boolean hasConnectedFruit(World world, BlockPos pos, BlockState state)
    {
        return getConnectedFruitDirection(world, pos, state) > 0;
    }

    @Unique
    private int getConnectedFruitDirection(World world, BlockPos pos, BlockState state)
    {

        Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
        Optional<Block> optional = registry.getOrEmpty(this.gourdBlock);
        Optional<Block> optional2 = registry.getOrEmpty(this.attachedStemBlock);


        for ( int iTempFacing = 2; iTempFacing < 6; iTempFacing++ )
        {

            pos.offset(Direction.byId(iTempFacing));

            if (optional.isPresent() && state == optional.get().getDefaultState() && optional2.isPresent())
            {
                return iTempFacing;
            }

            /**
            if ( state == gourdBlock.getDefaultState() &&
                    gourdBlock.getAttachedStem().getDefaultState().getBlock() != null)
            {
                return iTempFacing;
            }
             **/

        }

        return -1;
    }

}
