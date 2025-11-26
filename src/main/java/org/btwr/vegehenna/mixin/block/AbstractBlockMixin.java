package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.vegehenna.util.handler.FallingGourdBlockHandler;
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
                blockBelow.btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), state.getBlock());
            }
        }

        // Schedule block tick for gourd blocks
        FallingGourdBlockHandler.getInstance().onBlockAdded(state, world, pos);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        // Schedule block tick on neighbor update for gourd blocks
        FallingGourdBlockHandler.getInstance().getStateForNeighborUpdate(state, world, pos);
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // Apply the fall handler
        FallingGourdBlockHandler.getInstance().onScheduledTick(state, world, pos, ci);
    }

}