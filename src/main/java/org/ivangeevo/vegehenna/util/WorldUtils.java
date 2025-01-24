// FCMOD

package org.ivangeevo.vegehenna.util;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.FarmlandBlock.MOISTURE;

/**
 * Helper functions related to the world
 */
public class WorldUtils {

    public static void setFarmlandToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        boolean isTELoaded = FabricLoader.getInstance().isModLoaded("tough_environment");
        Block looseDirtBlock = Registries.BLOCK.get(Identifier.of("tough_environment", "dirt_loose"));
        BlockState blockState = Block.pushEntitiesUpBeforeBlockChange(state, isTELoaded ? looseDirtBlock.getDefaultState() : Blocks.DIRT.getDefaultState(), world, pos);
        world.setBlockState(pos, blockState);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, blockState));
    }

    public static void fertilizeFarmland(ItemUsageContext context, BlockPos pos, BlockState farmlandState) {
        context.getWorld().setBlockState(pos, ModBlocks.FARMLAND_FERTILIZED.getDefaultState().with(MOISTURE, farmlandState.get(MOISTURE)));
        context.getStack().decrement(1);
    }

}