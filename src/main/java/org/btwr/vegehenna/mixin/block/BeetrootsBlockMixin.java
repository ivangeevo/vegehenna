package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.block.interfaces.DailyGrowthCrop;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeetrootsBlock.class)
public abstract class BeetrootsBlockMixin extends CropBlock implements DailyGrowthCrop {

    public BeetrootsBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() >= 1) {
                cir.setReturnValue(WeedsBlock.SHAPE);
                return;
            }
        }

        cir.setReturnValue(NEW_BEETS_AGE_TO_SHAPE[this.getAge(state)]);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }

}