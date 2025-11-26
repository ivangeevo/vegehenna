package org.btwr.vegehenna.util.falling_blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class FallingBlockSupport {

    /**
     * Handles falling logic for blocks placed on unsafe supports.
     * Returns true if it handled (fall, break).
     */
    public static boolean checkForSupport(ServerWorld world, BlockPos pos, BlockState state) {
        BlockPos belowPos = pos.down();
        BlockState below = world.getBlockState(belowPos);

        boolean fallThrough = FallingBlock.canFallThrough(below);

        if (isSolidTop(world, belowPos, state, Direction.UP)) return false;

        if (fallThrough) {
            // Normal falling behavior
            FallingBlockEntity.spawnFromBlock(world, pos, state);
        } else {
            // Unsupported surface → break instantly
            world.breakBlock(pos, true);
        }
        return true;
    }

    public static boolean isSolidTop(WorldAccess world, BlockPos pos, BlockState state, Direction direction) {
        return state.isSideSolidFullSquare(world, pos, direction);
    }

}