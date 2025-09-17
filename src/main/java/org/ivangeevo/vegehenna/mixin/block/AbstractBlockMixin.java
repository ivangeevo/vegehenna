package org.ivangeevo.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.vegehenna.util.FallingBlockAPI;
import org.ivangeevo.vegehenna.util.FallingBlockSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Inject(method = "onBlockAdded", at = @At("TAIL"))
    private void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        // Torchflower special logic
        if (state.isOf(Blocks.TORCHFLOWER)) {
            BlockState belowState = world.getBlockState(pos.down());
            Block blockBelow = belowState.getBlock();
            if (blockBelow != null) {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), state.getBlock());
            }
        }

        if (FallingBlockAPI.isFallingLike(state)) {
            boolean safeSupport = FallingBlockSupport.isSolidTop(world, pos.down(), state, Direction.UP);
            int delay = safeSupport ? 10 : 2;
            world.scheduleBlockTick(pos, state.getBlock(), delay);
        }

    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (FallingBlockAPI.isFallingLike(state)) {
            boolean safeSupport = FallingBlockSupport.isSolidTop(world, pos.down(), state, Direction.UP);
            int delay = safeSupport ? 10 : 2;
            world.scheduleBlockTick(pos, state.getBlock(), delay);
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (FallingBlockAPI.isFallingLike(state)) {
            BlockPos below = pos.down();
            BlockState belowState = world.getBlockState(below);

            if (FallingBlock.canFallThrough(belowState) && pos.getY() >= world.getBottomY()) {
                FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, state);

                // Call custom handler if one exists
                FallingBlockAPI.applyFallHandler(world, pos, state, entity);

                ci.cancel();
            }
        }
    }

}
