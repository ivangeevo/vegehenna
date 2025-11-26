package org.ivangeevo.vegehenna.mixin.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    // PASS to allow other functionality besides placing, FAIL to remove all.
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getStack().isOf(Items.SWEET_BERRIES)) {
            cir.setReturnValue(ActionResult.PASS);
        }

        if (context.getStack().isOf(Items.SUGAR_CANE)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

}