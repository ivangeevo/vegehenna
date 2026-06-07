package org.btwr.vegehenna.mixin.bwt;

import com.bwt.blocks.HempCropBlock;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HempCropBlock.class, priority = 99999)
public abstract class HempCropBlockMixin {

    // Changes outline shape to the weeds one when they are present
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() >= 1) {
                cir.setReturnValue(WeedsBlock.SHAPE);
            }
        }
    }

    // Stops random ticks when it has weeds
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() <= 0) {
                ci.cancel();
            }
        }
    }
}