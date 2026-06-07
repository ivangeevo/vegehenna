package org.btwr.vegehenna.mixin.vanilla.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttachedStemBlock.class)
public abstract class AttachedStemBlockMixin extends PlantBlock {

    protected AttachedStemBlockMixin(Settings settings) {
        super(settings);
    }

    // Revert to an earlier AGE when its associated GourdBlock is harvested.
    @ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 7))
    private int injected(int value) {
        return 4;
    }

    @Inject(method = "getOutlineShape", at =  @At("HEAD"), cancellable = true)
    private void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() >= 1) {
                cir.setReturnValue(WeedsBlock.SHAPE);
            }
        }
    }

}
