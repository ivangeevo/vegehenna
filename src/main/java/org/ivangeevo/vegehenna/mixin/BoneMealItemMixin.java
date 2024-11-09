package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.FarmlandBlock.MOISTURE;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().isClient) return;

        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);

        // Case 1: Clicked directly on farmland
        if (state.isOf(Blocks.FARMLAND)) {
            fertilizeFarmland(context, pos, state, cir);
        }
        // Case 2: Clicked on a crop with farmland below
        else if (state.getBlock() instanceof CropBlock && context.getWorld().getBlockState(pos.down()).isOf(Blocks.FARMLAND)) {
            fertilizeFarmland(context, pos.down(), context.getWorld().getBlockState(pos.down()), cir);
        }
    }

    @Unique
    private void fertilizeFarmland(ItemUsageContext context, BlockPos pos, BlockState farmlandState, CallbackInfoReturnable<ActionResult> cir) {
        context.getWorld().setBlockState(pos, ModBlocks.FARMLAND_FERTILIZED.getDefaultState().with(MOISTURE, farmlandState.get(MOISTURE)));
        context.getStack().decrement(1);
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
