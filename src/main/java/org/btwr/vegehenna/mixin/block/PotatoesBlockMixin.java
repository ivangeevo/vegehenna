package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.btwr.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotatoesBlock.class)
public abstract class PotatoesBlockMixin extends CropBlock implements DailyGrowthCrop {

    public PotatoesBlockMixin(Settings settings) {
        super(settings);
    }

    // Custom outline shape
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(NEW_DEFAULT_AGE_TO_SHAPE[this.getAge(state)]);
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        return false;
    }

}