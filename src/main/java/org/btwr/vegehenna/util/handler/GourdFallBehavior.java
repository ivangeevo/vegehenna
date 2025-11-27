package org.btwr.vegehenna.util.handler;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import org.btwr.vegehenna.util.api.FallingBlockAPI;

import java.util.List;

public class GourdFallBehavior {

    private static final List<Block> GOURD_BLOCKS = List.of(Blocks.MELON, Blocks.PUMPKIN);

    public static void registerFallingBehavior() {
        for (var entry : GOURD_BLOCKS) {
            FallingBlockAPI.registerFallingBlock(entry, (world, pos, state, entity) -> {
                int fallDistance = 0;
                BlockPos.Mutable checkPos = pos.mutableCopy().move(0, -1, 0);

                // Scan down until we hit something solid
                while (checkPos.getY() >= world.getBottomY() && FallingBlock.canFallThrough(world.getBlockState(checkPos))) {
                    fallDistance++;
                    checkPos.move(0, -1, 0);
                }

                // checkPos is now the block it will land on (or world bottom)
                var landingState = world.getBlockState(checkPos);

                boolean canChanceBreak = world.random.nextFloat() < (fallDistance - 5) / 10f;
                boolean canBreak = fallDistance >= 5 && canChanceBreak;
                boolean shouldBreak = fallDistance >= 15 || canBreak;

                if (!FallingBlockAPI.isUnevenLandingSurface(world, checkPos, state, landingState)) {
                    if (shouldBreak) {
                        //entity.setDestroyedOnLanding();
                    }
                } else {
                    //Block.dropStacks(state, world, checkPos);
                }
            });
        }
    }

}