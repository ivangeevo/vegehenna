package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.tag.ModTags;
import org.btwr.vegehenna.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock {

    @Shadow public abstract BlockState getDefaultState();

    public BlockMixin(Settings settings) {
        super(settings);
    }

    // set farmland back to dirt on crop harvest and remove weeds
    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void onAfterBreakCrop(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci)
    {
        // If a crop that has weeds is broken
        if (state.isIn(ModTags.Blocks.CAN_GROW_WEEDS)) {
            if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
                if (weedsBE.getLevel() > 0) {
                    WeedsBlock.breakWeeds(world, pos, state, player, true);
                    weedsBE.removeWeeds();
                    return;
                }
            }
        }

        // Set to dirt if a crop-like block is broken
        if (state.isIn(BlockTags.CROPS) || state.isOf(Blocks.SHORT_GRASS)) {
            if (world.getBlockState(pos.down()).isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
                WorldUtils.setFarmlandToDirt(player, state, world, pos.down());
            }
        }
    }

}