package org.ivangeevo.vegehenna.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

    // remove normal usage for the milk bucket item.
    // TODO: Make the milk bucket act as a normal food instead.
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onGetUseAction(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
    {
        if (user.getMainHandStack().isOf(Items.MILK_BUCKET)) {
            cir.setReturnValue(TypedActionResult.fail(user.getMainHandStack()));
        }

    }
}
