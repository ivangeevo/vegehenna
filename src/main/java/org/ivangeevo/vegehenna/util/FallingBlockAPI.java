package org.ivangeevo.vegehenna.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class FallingBlockAPI {

    // Falling-like block registrations
    private static final Map<Block, FallHandler> FALLING_BLOCKS = new HashMap<>();

    public interface FallHandler {
        /**
         * Called before a falling block spawns.
         * Allows checking fall distance, altering entity state, etc.
         *
         * @param world The world
         * @param pos The starting pos
         * @param state The block state
         * @param entity The spawned FallingBlockEntity
         */
        void onFall(ServerWorld world, BlockPos pos, BlockState state, FallingBlockEntity entity);
    }

    /**
     * Register a block as falling-like with a custom fall handler.
     */
    public static void registerFallingBlock(Block block, FallHandler handler) {
        FALLING_BLOCKS.put(block, handler);
    }

    /**
     * Is the block registered as falling-like?
     */
    public static boolean isFallingLike(BlockState state) {
        return FALLING_BLOCKS.containsKey(state.getBlock());
    }

    /**
     * Apply custom handler logic after entity spawn, if present.
     */
    public static void applyFallHandler(ServerWorld world, BlockPos pos, BlockState state, FallingBlockEntity entity) {
        FallHandler handler = FALLING_BLOCKS.get(state.getBlock());
        if (handler != null) {
            handler.onFall(world, pos, state, entity);
        }
    }
}
