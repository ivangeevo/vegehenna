package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TorchflowerBlock.class)
public abstract class TorchFlowerBlockMixin extends CropBlock implements DailyGrowthCrop {
    public TorchFlowerBlockMixin(Settings settings) {
        super(settings);
    }

    //@Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }
}
