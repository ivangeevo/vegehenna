package org.ivangeevo.vegehenna.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.ivangeevo.vegehenna.tag.ModTags;
import org.ivangeevo.vegehenna.util.falling_blocks.FallingBlockSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {

    /**
     * Replace/extend FallingBlock's scheduledTick behavior:
     * spawn a FallingBlockEntity if the block below is 'unsafe' (no solid top face) OR canFallThrough.
     * Cancel vanilla method when we spawn to avoid double-spawning.
     */
    //@Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!state.isIn(ModTags.Blocks.NEEDS_SUPPORT_BLOCK)) return;
        if (FallingBlockSupport.checkForSupport(world, pos, state)) {
            ci.cancel();
        }
    }

}