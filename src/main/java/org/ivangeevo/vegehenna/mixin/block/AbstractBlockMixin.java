package org.ivangeevo.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements LandingBlock
{

    @Shadow protected abstract Block asBlock();

    @Inject(method = "onBlockAdded", at = @At("TAIL"))
    private void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (state.isOf(Blocks.TORCHFLOWER)) {
            BlockState belowState = world.getBlockState(pos.down());
            Block blockBelow = belowState.getBlock();

            if (blockBelow != null) {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), state.getBlock());
            }
        }

        if (state.isOf(Blocks.MELON) || state.isOf(Blocks.PUMPKIN)) {
            world.scheduleBlockTick(pos, (Block)(Object)this.asBlock(), 2);
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.isOf(Blocks.MELON) || state.isOf(Blocks.PUMPKIN)) {
            world.scheduleBlockTick(pos, (Block)(Object)this.asBlock(), 2);
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        this.scheduleTickGourdBlock(world, pos, state, Blocks.MELON);
    }

    @Unique
    private void scheduleTickGourdBlock(World world, BlockPos pos, BlockState state, Block block) {
        if (!state.isOf(block)) return;
        if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            //entity.setAttached(VegehennaMod.FALL_BLOCK_SPAWN_Y, entity.getBlockY());

            if (state.isOf(Blocks.MELON)) {

                int fallDistance = 0;
                boolean shouldBreak;

                // Look ahead and simulate fall
                BlockPos.Mutable checkPos = pos.mutableCopy().move(0, -1, 0);
                while (checkPos.getY() >= world.getBottomY() && canFallThrough(world.getBlockState(checkPos))) {
                    fallDistance++;
                    checkPos.move(0, -1, 0);
                }

                shouldBreak = fallDistance >= 15 || (fallDistance >= 5 && world.random.nextFloat() < (fallDistance - 5) / 10f);

                if (shouldBreak) {
                    entity.setDestroyedOnLanding();
                }

            }

        }
    }

    @Unique
    private static boolean canFallThrough(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isLiquid() || state.isReplaceable();
    }

}
