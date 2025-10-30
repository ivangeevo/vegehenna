package org.ivangeevo.vegehenna.mixin.block;

import btwr.btwr_sl.lib.interfaces.added.BlockAdded;
import btwr.btwr_sl.tag.BTWRConventionalTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements BlockAdded, LandingBlock
{

    @Shadow public abstract BlockState getDefaultState();

    public BlockMixin(Settings settings) {
        super(settings);
    }

    // set farmland back to dirt on crop harvest
    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void onAfterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci)
    {
        if (state.isIn(BlockTags.CROPS) && world.getBlockState(pos.down()).isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
            WorldUtils.setFarmlandToDirt(player, state, world, pos.down());
        }
    }

}
