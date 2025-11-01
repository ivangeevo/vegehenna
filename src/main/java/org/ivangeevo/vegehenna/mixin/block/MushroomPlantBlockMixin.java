package org.ivangeevo.vegehenna.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MushroomPlantBlock.class)
public abstract class MushroomPlantBlockMixin extends PlantBlock
{
    @Shadow public abstract boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random);

    protected MushroomPlantBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyReturnValue(method = "isFertilizable", at = @At("RETURN"))
    boolean cancelFertilizable(boolean original) {
        return false;
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockState defaultState = this.getDefaultState();
        boolean isBrownMushroom = defaultState.isOf(Blocks.BROWN_MUSHROOM);
        boolean isRedMushroom = defaultState.isOf(Blocks.BROWN_MUSHROOM);

        if (isBrownMushroom || isRedMushroom) {
            if (isBrownMushroom) {
                // only allow growth in the overworld
                if (world.getDimensionEntry().matchesId(DimensionTypes.OVERWORLD_ID)) {
                    this.onTickMushroom(state, world, pos, ci);
                }
            }

            if (isRedMushroom) {
                // Don't allow growing in the end
                if (!world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID)) {
                    this.onTickMushroom(state, world, pos, ci);
                }
            }

            ci.cancel();
        }
    }

    @Unique
    private void onTickMushroom(BlockState state, ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM) && world.getRandom().nextInt(50) == 0) {
            // mushrooms growing on mycelium have a chance of sprouting into giant mushrooms
            trySpawningBigMushroom(world, pos, state, world.getRandom());
        } else {
            if (state.isOf(Blocks.BROWN_MUSHROOM))  {
                checkForSpreadBrownMushroom(world, pos, state, world.getRandom());
            } else if (state.isOf(Blocks.RED_MUSHROOM)) {
                checkForSpreadRedMushroom(world, pos, state, world.getRandom());
            }
        }
    }

    @Unique // Extracted canPlaceAt() from the same class for calling again
    protected boolean canPlaceBrownAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)
                || world.getBaseLightLevel(pos, 0) <= 0 && this.canPlantOnTop(blockState, world, blockPos);
    }

    @Unique
    boolean canSpreadToOrFromLocation(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return blockBelow == Blocks.MYCELIUM || world.getLightLevel(pos) == 0;
    }

    // basically a copy/paste of the tick method with additional requirements that brown mushrooms can only grow in complete darkness
    @Unique
    void checkForSpreadBrownMushroom(World world, BlockPos pos, BlockState state, Random random) {
        if (random.nextInt(25) == 0 && canSpreadToOrFromLocation(world, pos)) {
            int i = 5;
            int j = 4;

            for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos).isOf(this)) {
                    if (--i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            for (int k = 0; k < 4; k++) {
                if (world.isAir(blockPos2) && canPlaceBrownAt(state, world, blockPos2)) {
                    pos = blockPos2;
                }

                blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (world.isAir(blockPos2) && canPlaceBrownAt(state, world, blockPos2) && canSpreadToOrFromLocation(world, pos)) {
                world.setBlockState(blockPos2, state, Block.NOTIFY_LISTENERS);
            }
        }
    }

    // Basically the same as the vanilla tick method code
    @Unique
    void checkForSpreadRedMushroom(World world, BlockPos pos, BlockState state, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;

            for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos).isOf(this)) {
                    if (--i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            for (int k = 0; k < 4; k++) {
                if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                    pos = blockPos2;
                }

                blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                world.setBlockState(blockPos2, state, Block.NOTIFY_LISTENERS);
            }
        }
    }

}
