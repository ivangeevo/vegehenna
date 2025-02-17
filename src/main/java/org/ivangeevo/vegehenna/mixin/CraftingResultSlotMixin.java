package org.ivangeevo.vegehenna.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.collection.DefaultedList;
import org.ivangeevo.vegehenna.item.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Shadow @Final private RecipeInputInventory input;

    // Makes chocolate milk recipes to consume the bucket instead or returning the remainder (empty bucket)
    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() == ModItems.CHOCOLATE_MILK) {
            RecipeInputInventory input = this.input;
            DefaultedList<ItemStack> remainingStacks = player.getWorld().getRecipeManager()
                    .getRemainingStacks(RecipeType.CRAFTING, input.createPositionedRecipeInput().input(), player.getWorld());

            for (int i = 0; i < remainingStacks.size(); i++) {
                ItemStack remainder = remainingStacks.get(i);
                if (remainder.getItem() == Items.MILK_BUCKET) {
                    remainingStacks.set(i, ItemStack.EMPTY);
                }
            }

            for (int i = 0; i < remainingStacks.size(); i++) {
                if (!remainingStacks.get(i).isEmpty()) {
                    input.setStack(i, remainingStacks.get(i));
                }
            }
        }
    }
}
