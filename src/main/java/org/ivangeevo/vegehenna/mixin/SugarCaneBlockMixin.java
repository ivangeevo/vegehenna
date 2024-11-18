package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.ivangeevo.vegehenna.block.blocks.SugarCaneRootsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin
{

    // TODO: Fix placed(naturally generated) sugarcane blocks on top of sugar cane root blocks
    //  will eventually break on random tick for some reason
    //@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void onCanPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        BlockState blockState = world.getBlockState(pos.down());
        if (!(blockState.getBlock() instanceof SugarCaneRootsBlock)) {
            blockState.isOf((SugarCaneBlock) (Object) this);
        }

        cir.setReturnValue(false);
    }
}

