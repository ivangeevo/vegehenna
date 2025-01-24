package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.FarmlandBlock.MOISTURE;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {

    // Fertilize farmland on use
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().isClient) return;

        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);

       // Clicked directly on farmland
        if (state.isOf(Blocks.FARMLAND)) {
            WorldUtils.fertilizeFarmland(context, pos, state);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
        // Clicked on a crop with farmland below
        else if (state.getBlock() instanceof CropBlock && context.getWorld().getBlockState(pos.down()).isOf(Blocks.FARMLAND)) {
            WorldUtils.fertilizeFarmland(context, pos.down(), context.getWorld().getBlockState(pos.down()));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

}
