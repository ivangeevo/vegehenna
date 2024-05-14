package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin extends MiningToolItem {
    protected HoeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    // Make the hoe slower.
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return super.getMiningSpeedMultiplier(stack, state) / 3;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)

    {
        if (stack.isIn(BTWRConventionalTags.Items.MODERN_HOES) || stack.isIn(BTWRConventionalTags.Items.ADVANCED_HOES))
        {
            if (miner instanceof PlayerEntity playerEntity)
            {
                ItemUsageContext context = new ItemUsageContext(playerEntity, Hand.MAIN_HAND, new BlockHitResult(playerEntity.getPos(), Direction.UP, pos, false));

                stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }
        }

        return false;

    }
}
