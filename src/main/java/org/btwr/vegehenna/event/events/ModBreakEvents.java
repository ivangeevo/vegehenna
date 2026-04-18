package org.btwr.vegehenna.event.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.tag.ModTags;
import org.btwr.vegehenna.util.WorldUtils;

public class ModBreakEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // Remove weeds if a crop has them is broken and convert the farmland to dirt
            if (state.isIn(ModTags.Blocks.CAN_GROW_WEEDS)) {
                if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
                    if (weedsBE.getLevel() > 0) {
                        WeedsBlock.breakWeeds(world, pos, state, player, true);
                        weedsBE.removeWeeds();
                        convertFarmlandToDirt(world, pos, state, player);
                        return;
                    }
                }
            }

            // Set farmland to dirt if a crop-like block is broken
            if (state.isIn(BlockTags.CROPS) || state.isOf(Blocks.SHORT_GRASS)) {
                if (world.getBlockState(pos.down()).isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
                    convertFarmlandToDirt(world, pos, state, player);
                }
            }
        });
    }

    private static void convertFarmlandToDirt(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // Only convert to dirt outside of creative
        if (!player.isCreative()) {
            WorldUtils.setFarmlandToDirt(player, state, world, pos.down());
        }
    }
}
