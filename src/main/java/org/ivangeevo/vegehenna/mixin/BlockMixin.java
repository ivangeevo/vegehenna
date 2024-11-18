package org.ivangeevo.vegehenna.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tough_environment.block.ModBlocks;

@Mixin(Block.class)
public abstract class BlockMixin
{
    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void onAfterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci)
    {
        if (state.isIn(BlockTags.CROPS))
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if (blockBelow.getDefaultState().isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS))
            {
                if (FabricLoader.getInstance().isModLoaded("tough_environment"))
                {
                    world.setBlockState(pos.down(), ModBlocks.DIRT_LOOSE.getDefaultState());
                }
                else
                {
                    world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState());
                }
            }
        }
    }

}
