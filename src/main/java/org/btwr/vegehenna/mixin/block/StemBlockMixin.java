package org.btwr.vegehenna.mixin.block;

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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.btwr.shared_library.tag.BTWRConventionalTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends PlantBlock
{
    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract boolean canGrow(World world, Random random, BlockPos pos, BlockState state);
    @Shadow @Final private RegistryKey<Block> gourdBlock;
    @Shadow @Final private RegistryKey<Block> attachedStemBlock;
    @Shadow @Final public static int MAX_AGE;

    public StemBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            checkForGrowth(world, pos, state, random);
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
    private void checkForGrowth(World world, BlockPos pos, BlockState state, Random rand) {
        if (this.btwr$getWeedsGrowthLevel(world, pos) == 0 && world.getLightLevel( pos.up() ) >= 9) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if (blockBelow != null && blockBelow.btwr$isBlockHydratedForPlantGrowthOn(world, pos.down())) {
                float fGrowthChance = 0.2F * blockBelow.btwr$getPlantGrowthOnMultiplier(world, pos.down(), this);

                if (rand.nextFloat() <= fGrowthChance) {

                    if (state.get(AGE) < MAX_AGE) {
                        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1));
                    }
                    else if (state.get(AGE) == MAX_AGE) {
                        int iTargetFacing = 0;

                        if (hasSpaceToGrow(world, pos, state)) {
                            // if the plant doesn't have space around it to grow,
                            // the fruit will crush its own stem

                            iTargetFacing = rand.nextInt( 4 ) + 2;

                            pos.offset(Direction.byId(iTargetFacing));
                        }

                        if (canGrowFruitAt(world, pos, state)) {
                            Direction direction = Direction.Type.HORIZONTAL.random(rand);
                            BlockPos blockPos = pos.offset(direction);
                            BlockState blockState = world.getBlockState(blockPos.down());

                            if (world.getBlockState(blockPos).isAir() && ( blockState.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS) || blockState.isIn(BlockTags.DIRT) ))
                            {
                                Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
                                Optional<Block> optional = registry.getOrEmpty(this.gourdBlock);
                                Optional<Block> optional2 = registry.getOrEmpty(this.attachedStemBlock);

                                if (optional.isPresent() && optional2.isPresent()) {
                                    world.setBlockState(blockPos, optional.get().getDefaultState());
                                    world.setBlockState(pos, optional2.get().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                                }

                                blockBelow.btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    protected boolean hasSpaceToGrow(World world, BlockPos pos, BlockState state) {
        for (int iTargetFacing = 2; iTargetFacing <= 5; iTargetFacing++ ) {

            pos.offset(Direction.byId(iTargetFacing));

            if (canGrowFruitAt(world, pos, state)) {
                return true;
            }
        }

        return false;
    }

    @Unique
    protected boolean canGrowFruitAt(World world, BlockPos pos, BlockState state) {

        if (state.isReplaceable() ||
                (state.getBlock() != null /** &&  state.getBlock() instanceof  **/ &&
                        state != Blocks.COCOA.getDefaultState()))
        {
            return hasLargeCenterHardPointToFacing(world, pos.down(), Direction.UP) ||
                    canGrow(world, world.getRandom(), pos.down(), state);
        }

        return false;
    }

    private static boolean hasLargeCenterHardPointToFacing(WorldAccess blockAccess, BlockPos pos, Direction facing, boolean bIgnoreTransparency)
    {
        return blockAccess.getBlockState(pos).isSideSolidFullSquare(blockAccess, pos, facing);
    }

    private static boolean hasLargeCenterHardPointToFacing(WorldAccess blockAccess, BlockPos pos, Direction facing) {
        return hasLargeCenterHardPointToFacing(blockAccess, pos, facing, false);
    }

}