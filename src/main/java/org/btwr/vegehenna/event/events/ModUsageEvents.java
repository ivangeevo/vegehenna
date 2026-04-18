package org.btwr.vegehenna.event.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.util.WorldUtils;

public class ModUsageEvents {

    public static void register() {
        // Right-click on crop blocks to try removing weeds
        UseBlockCallback.EVENT.register(ModUsageEvents::tryRemovingWeeds);
        // Right-click with bone meal on crops or farmland
        UseBlockCallback.EVENT.register(ModUsageEvents::tryFertilizeFarmland);
        // Disables drinking a milk bucket
        UseItemCallback.EVENT.register(ModUsageEvents::disableMilkBucketUsage);
    }

    private static ActionResult tryRemovingWeeds(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (canWeedsShareSpaceWith(state)) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
                if (weedsBE.getLevel() <= 0) {
                    return ActionResult.PASS;
                }

                WeedsBlock.breakWeeds(world, pos, state, player, true);
                weedsBE.removeWeeds();
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private static ActionResult tryFertilizeFarmland(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(Items.BONE_MEAL)) return ActionResult.PASS;

        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);

        ItemUsageContext context = new ItemUsageContext(player, hand, hit);

        // Clicked directly on farmland
        if (state.isOf(Blocks.FARMLAND)) {
            WorldUtils.fertilizeFarmland(context, pos, state);
            return ActionResult.SUCCESS;
        }
        // Clicked on a crop with farmland below
        else if (state.getBlock() instanceof CropBlock && world.getBlockState(pos.down()).isOf(Blocks.FARMLAND)) {
            WorldUtils.fertilizeFarmland(context, pos.down(), world.getBlockState(pos.down()));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static TypedActionResult<ItemStack> disableMilkBucketUsage(PlayerEntity player, World world, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isOf(Items.MILK_BUCKET)) {
            return TypedActionResult.fail(handStack);
        }
        return TypedActionResult.pass(handStack);
    }

    private static boolean canWeedsShareSpaceWith(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof AttachedStemBlock;
    }
}
