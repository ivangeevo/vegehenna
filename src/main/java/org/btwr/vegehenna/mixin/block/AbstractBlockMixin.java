package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.util.handler.FallingBlockHandler;
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

        // Schedule block tick for custom falling blocks
        FallingBlockHandler.getInstance().onBlockAdded(state, world, pos);
    }

    /** Schedule block tick on neighbor update for custom falling blocks **/
    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        FallingBlockHandler.getInstance().getStateForNeighborUpdate(state, world, pos);
    }

    // Apply the fall handler
    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        FallingBlockHandler.getInstance().onScheduledTick(state, world, pos, ci);
    }

    // Clean up weed data if farmland is destroyed
    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void onRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (state.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
            // if broken or replaced with another state
            if (newState.isAir() || newState != state) {
                if (world.getBlockEntity(pos) instanceof WeedsBlockEntity weedsBE)
                    weedsBE.removeWeeds();
            }
        }
    }

}