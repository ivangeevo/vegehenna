package org.btwr.vegehenna.mixin.vanilla.block;

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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.tag.ModTags;
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
public abstract class StemBlockMixin extends PlantBlock {
    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract boolean canGrow(World world, Random random, BlockPos pos, BlockState state);
    @Shadow @Final private RegistryKey<Block> gourdBlock;
    @Shadow @Final private RegistryKey<Block> attachedStemBlock;
    @Shadow @Final public static int MAX_AGE;

    public StemBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() > 0) {
                cir.setReturnValue(WeedsBlock.SHAPE);
            }
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            // Stop ticking if weeds are present
            BlockPos posBelow = pos.down();
            if (world.getBlockEntity(posBelow) instanceof WeedsBlockEntity be && be.getLevel() > 0) {
                ci.cancel();
                return;
            }

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
        if (this.btwr$getWeedsGrowthLevel(world, pos) == 0 && world.getLightLevel(pos.up()) >= 9) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if (blockBelow != null && blockBelow.btwr$isBlockHydratedForPlantGrowthOn(world, pos.down())) {
                float fGrowthChance = 0.2F * blockBelow.btwr$getPlantGrowthOnMultiplier(world, pos.down(), this);

                if (rand.nextFloat() <= fGrowthChance) {

                    if (state.get(AGE) < MAX_AGE) {
                        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1));
                    }
                    else {
                        // Default target is the stem's own position (fruit crushes stem)
                        BlockPos targetPos = pos;
                        Direction targetFacing = null;

                        if (hasSpaceToGrow(world, pos, state)) {
                            // Only pick a random offset if there's actually space
                            targetFacing = Direction.Type.HORIZONTAL.random(rand);
                            targetPos = pos.offset(targetFacing);
                        }

                        if (canGrowFruitAt(world, targetPos, state)) {
                            Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
                            Optional<Block> optionalFruit = registry.getOrEmpty(this.gourdBlock);
                            Optional<Block> optionalAttached = registry.getOrEmpty(this.attachedStemBlock);

                            if (optionalFruit.isPresent() && optionalAttached.isPresent()) {
                                // Notify block below stem before placing fruit
                                blockBelow.btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), this);

                                // Place the fruit at the target position
                                world.setBlockState(targetPos, optionalFruit.get().getDefaultState());

                                // Only convert stem to attached stem if fruit grew to a neighbor
                                if (targetFacing != null) {
                                    world.setBlockState(pos, optionalAttached.get().getDefaultState()
                                            .with(HorizontalFacingBlock.FACING, targetFacing));
                                }
                                // else: fruit replaced the stem itself, no stem state to set
                            }
                        }
                    }
                }
            }
        }
    }
    @Unique
    protected boolean hasSpaceToGrow(World world, BlockPos pos, BlockState state) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (canGrowFruitAt(world, pos.offset(direction), state)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    protected boolean canGrowFruitAt(World world, BlockPos targetPos, BlockState state) {
        BlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();

        boolean targetIsValid = targetState.isReplaceable() ||
                (targetBlock != Blocks.COCOA &&
                        (targetBlock instanceof PlantBlock || targetState.isIn(BlockTags.REPLACEABLE_BY_TREES))
                );

        if (targetIsValid) {
            BlockPos groundPos = targetPos.down();
            BlockState groundState = world.getBlockState(groundPos);
            boolean isFullSolidSide = groundState.isSideSolidFullSquare(world, groundPos, Direction.UP);

            return isFullSolidSide || groundState.isIn(ModTags.Blocks.CAN_DOMESTICATED_CROPS_GROW_ON);
        }

        return false;
    }
}