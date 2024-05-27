package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AliasedBlockItem.class)
public abstract class AliasedBlockItemMixin extends BlockItem
{
    public AliasedBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    // Change the placement block of CARROT to the modded block.
    @Override
    protected boolean place(ItemPlacementContext context, BlockState state)
    {
        if (context.getStack().isOf(Items.CARROT))
        {
            BlockState replacementState = ModBlocks.CARROT_FLOWERING.getDefaultState();
            return context.getWorld().setBlockState(context.getBlockPos(), replacementState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        }

        return super.place(context, state);
    }

}
