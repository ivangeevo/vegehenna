package org.ivangeevo.vegehenna.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.ivangeevo.vegehenna.mixin.ServerChunkLoadingManagerAccessor;

public class CropGrowthHandler {
    private static long lastDay = -1;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) {
                return;
            }

            long timeOfDay = world.getTimeOfDay() % 24000L;

            // Check for new day
            if (timeOfDay < 1000 && lastDay != -1 && lastDay > timeOfDay) {
                resetGrowthFlags(serverWorld);
            }

            if (timeOfDay < 1000) {
                lastDay = world.getTime();
            }
        });
    }

    private static void resetGrowthFlags(ServerWorld world) {
        ServerChunkManager chunkManager = world.getChunkManager();
        ServerChunkLoadingManager loadingManager = chunkManager.chunkLoadingManager;

        // Use the accessor to get the chunk holders
        Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders =
                ((ServerChunkLoadingManagerAccessor) loadingManager).getChunkHolders();

        // Iterate over the loaded chunks
        for (ChunkHolder chunkHolder : chunkHolders.values()) {
            WorldChunk chunk = chunkHolder.getWorldChunk();
            if (chunk != null) {
                iterateChunkBlocks(chunk, world);
            }
        }
    }

    private static void iterateChunkBlocks(WorldChunk chunk, ServerWorld world) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getBottomY(); y < world.getTopY(); y++) {
                    mutable.set(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z);
                    BlockState state = world.getBlockState(mutable);

                    if (state.getBlock() instanceof CropBlock) {
                        // Reset the HAS_GROWN_TODAY property
                        if (state.contains(DailyGrowthCrop.HAS_GROWN_TODAY)) {
                            world.setBlockState(mutable, state.with(DailyGrowthCrop.HAS_GROWN_TODAY, false), Block.NOTIFY_ALL);
                        }
                    }
                }
            }
        }
    }
}
