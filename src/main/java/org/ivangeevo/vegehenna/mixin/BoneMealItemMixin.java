package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.FarmlandBlock.MOISTURE;
import static org.ivangeevo.vegehenna.block.interfaces.FarmlandBlockAdded.FERTILIZED;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin
{
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        if (state.isOf(Blocks.FARMLAND))
        {

            if (!context.getWorld().isClient)
            {
                context.getWorld().setBlockState(pos, ModBlocks.FARMLAND_FERTILIZED.getDefaultState().with(MOISTURE, state.get(MOISTURE)));
                context.getStack().decrement(1);

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
