package org.btwr.vegehenna.mixin.vanilla.block;

import net.minecraft.block.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.tag.ModTags;
import org.btwr.vegehenna.util.handler.FallingBlockHandler;
import org.btwr.vegehenna.util.handler.GourdExplodeBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Unique
    private static final double PROJECTILE_SPEED_SQUARED_TO_EXPLODE = 1.10D;

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

    @Inject(method = "onProjectileHit", at = @At("HEAD"))
    private void onProjectileHitGourd(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci) {
        if (!state.isIn(ModTags.Blocks.GOURD_BLOCKS)) return;
        if (world.isClient) return;

        BlockPos blockPos = hit.getBlockPos();

        // Strong projectiles can break it
        if (isStrongProjectile(projectile) || projectile.getVelocity().lengthSquared() >= PROJECTILE_SPEED_SQUARED_TO_EXPLODE) {
            GourdExplodeBehavior.onProjectileHit(world, state, blockPos);
        } else {
            // Weak projectiles only play impact sound
            GourdExplodeBehavior.onProjectileWeakHit(world, blockPos);
        }


    }

    @Unique
    private static boolean isStrongProjectile(ProjectileEntity projectile) {
        return projectile instanceof TridentEntity
                || projectile instanceof WindChargeEntity
                || projectile instanceof BreezeWindChargeEntity;
    }
}