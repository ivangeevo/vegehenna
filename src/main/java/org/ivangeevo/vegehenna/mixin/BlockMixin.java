package org.ivangeevo.vegehenna.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock
{


    public BlockMixin(Settings settings) {
        super(settings);
    }

    // set farmland back to dirt on crop harvest
    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void onAfterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci)
    {
        if (state.isIn(BlockTags.CROPS)) {
            setFarmLandToDirt(player, state, world, pos.down());
        }
    }

    @Unique
    private static void setFarmLandToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        boolean isTELoaded = FabricLoader.getInstance().isModLoaded("tough_environment");
        Block looseDirtBlock = Registries.BLOCK.get(Identifier.of("tough_environment", "dirt_loose"));
        BlockState blockState = Block.pushEntitiesUpBeforeBlockChange(state, isTELoaded ? looseDirtBlock.getDefaultState() : Blocks.DIRT.getDefaultState(), world, pos);
        world.setBlockState(pos, blockState);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, blockState));
    }

}
