package org.ivangeevo.vegehenna.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;

/** Handles resetting the growth flag for daily growth crops if the time is wrapped around for some reason **/
public class HasCropGrownHandler
{

    private static long lastTime = 0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long currentTime = world.getTimeOfDay() % 24000L;

            // If time wrapped around (e.g. due to sleep, or time add), night was skipped
            if (lastTime > currentTime) {
                resetDailyGrowthFlags(world);
            }

            lastTime = currentTime;
        });
    }

    private static void resetDailyGrowthFlags(ServerWorld world) {
        int simDistChunks = world.getServer().getPlayerManager().getSimulationDistance();

        world.getPlayers().forEach(player -> {
            BlockPos playerPos = player.getBlockPos();
            int playerChunkX = playerPos.getX() >> 4;
            int playerChunkZ = playerPos.getZ() >> 4;

            for (int dx = -simDistChunks; dx <= simDistChunks; dx++) {
                for (int dz = -simDistChunks; dz <= simDistChunks; dz++) {
                    int chunkX = playerChunkX + dx;
                    int chunkZ = playerChunkZ + dz;

                    var chunk = world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                    if (chunk == null) continue;

                    for (int y = playerPos.getY() - 2; y <= playerPos.getY() + 2; y++) {
                        if (y < world.getBottomY() || y >= world.getTopY()) continue;

                        for (int bx = 0; bx < 16; bx++) {
                            for (int bz = 0; bz < 16; bz++) {
                                BlockPos pos = new BlockPos((chunkX << 4) + bx, y, (chunkZ << 4) + bz);
                                BlockState state = chunk.getBlockState(pos);

                                if (state.getBlock() instanceof DailyGrowthCrop && state.contains(DailyGrowthCrop.HAS_GROWN_TODAY)) {
                                    world.setBlockState(pos, state.with(DailyGrowthCrop.HAS_GROWN_TODAY, false), 2);
                                }
                            }
                        }
                    }
                }
            }
        });
    }



}
