package org.ivangeevo.vegehenna.util;

import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.vegehenna.tag.ModTags;

public class GourdBlockHandler {

    public static void registerFallingBehavior() {

        // Register fall behavior for gourd blocks
        for (var entry : Registries.BLOCK.iterateEntries(ModTags.Blocks.GOURD_BLOCKS)) {
            Block block = entry.value();

            FallingBlockAPI.registerFallingBlock(block, ((world, pos, state, entity) -> {
                int fallDistance = 0;
                BlockPos.Mutable checkPos = pos.mutableCopy().move(0, -1, 0);
                while (checkPos.getY() >= world.getBottomY() && FallingBlock.canFallThrough(world.getBlockState(checkPos))) {
                    fallDistance++;
                    checkPos.move(0, -1, 0);
                }

                boolean canBreak = fallDistance >= 5 && world.random.nextFloat() < (fallDistance - 5) / 10f;
                boolean shouldBreak = fallDistance >= 15 || canBreak;

                if (shouldBreak) {
                    entity.setDestroyedOnLanding();
                }
            }
            ));
        }

    }

}
