package org.btwr.vegehenna.util.handler;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.event.ModEvents;
import org.btwr.vegehenna.item.ModItems;
import org.btwr.vegehenna.sound.ModSoundEvents;
import org.btwr.vegehenna.util.WorldUtils;
import org.btwr.vegehenna.util.api.FallingBlockAPI;

import java.util.List;

public class GourdFallBehavior {

    private static final List<Block> GOURD_BLOCKS = List.of(Blocks.MELON, Blocks.PUMPKIN);

    public static void register() {
        for (var entry : GOURD_BLOCKS) {
            FallingBlockAPI.registerFallingBlock(entry, ((world, pos, state, entity) -> {}));
            /**
            FallingBlockAPI.registerLandingBlock(entry, (world, pos, state, entity, blocksFallen) -> {
                BlockState atPos = world.getBlockState(pos);
                BlockState above = world.getBlockState(pos.up());

                boolean isPlantAbove = above.getBlock() instanceof PlantBlock;
                boolean isPlantAtPos = atPos.getBlock() instanceof PlantBlock;
                boolean isPlant = isPlantAtPos || isPlantAbove;
                BlockPos plantPos = isPlantAbove ? pos.up() : pos;

                boolean isUnevenSurface = atPos.getCollisionShape(world, pos)
                        .getMax(Direction.Axis.Y) < 1.0f && !FallingBlock.canFallThrough(atPos);

                boolean canChanceBreak = world.random.nextFloat() < (blocksFallen - 5) / 10f;
                boolean shouldBreak = blocksFallen >= 15 || (blocksFallen >= 5 && canChanceBreak);

                if (isPlant) {
                    Block.dropStacks(world.getBlockState(plantPos), world, plantPos);
                    world.setBlockState(plantPos, Blocks.AIR.getDefaultState());

                    BlockState underCrop = world.getBlockState(plantPos.down());
                    if (underCrop.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
                        WorldUtils.setFarmlandToDirt(entity, state, world, plantPos.down());
                    }

                    if (shouldBreak) {
                        if (state.isOf(Blocks.MELON)) {
                            onGourdFallDestroyed(world, state, entity.getBoundingBox(), ModItems.MASHED_MELON, 2);
                        }
                        if (state.isOf(Blocks.PUMPKIN)) {
                            onGourdFallDestroyed(world, state, entity.getBoundingBox(), Items.PUMPKIN_SEEDS, 2);
                        }
                        entity.discard();
                    } else {
                        world.setBlockState(plantPos, state);
                        entity.discard();
                    }
                } else if (shouldBreak) {
                    if (state.isOf(Blocks.MELON)) {
                        onGourdFallDestroyed(world, state, entity.getBoundingBox(), ModItems.MASHED_MELON, 2);
                    }
                    if (state.isOf(Blocks.PUMPKIN)) {
                        onGourdFallDestroyed(world, state, entity.getBoundingBox(), Items.PUMPKIN_SEEDS, 2);
                    }
                    entity.discard();
                } else if (isUnevenSurface) {
                    // Couldn't place on uneven surface, not enough fall to break — drop as normal item
                    entity.dropItem(state.getBlock());
                    entity.discard();
                } else {
                    world.setBlockState(pos, state);
                    entity.discard();
                }
            });
             **/
            FallingBlockAPI.registerLandingBlock(entry, (world, pos, state, entity, blocksFallen) -> {
                BlockState atPos = world.getBlockState(pos);
                BlockState above = world.getBlockState(pos.up());

                boolean isPlantAtPos = atPos.getBlock() instanceof PlantBlock;
                boolean isPlantAbove = above.getBlock() instanceof PlantBlock;
                BlockPos plantPos = isPlantAbove ? pos.up() : pos;

                boolean isPlant = isPlantAtPos || isPlantAbove;
                boolean isUnevenSurface = atPos.getCollisionShape(world, pos).getMax(Direction.Axis.Y) < 1.0f
                        && !FallingBlock.canFallThrough(atPos);

                boolean shouldBreak = blocksFallen >= 15
                        || (blocksFallen >= 5 && world.random.nextFloat() < (blocksFallen - 5) / 10f);

                if (isPlant) {
                    destroyPlant(world, state, entity, plantPos, shouldBreak);
                } else if (shouldBreak) {
                    smashGourd(world, state, entity);
                } else if (isUnevenSurface) {
                    entity.dropItem(state.getBlock());
                    entity.discard();
                } else {
                    world.setBlockState(pos, state);
                    entity.discard();
                }
            });

        }
    }

    private static void destroyPlant(ServerWorld world, BlockState state, FallingBlockEntity entity, BlockPos plantPos, boolean shouldBreak) {
        Block.dropStacks(world.getBlockState(plantPos), world, plantPos);
        world.setBlockState(plantPos, Blocks.AIR.getDefaultState());

        BlockState underCrop = world.getBlockState(plantPos.down());
        if (underCrop.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
            WorldUtils.setFarmlandToDirt(entity, state, world, plantPos.down());
        }

        if (shouldBreak) {
            smashGourd(world, state, entity);
        } else {
            world.setBlockState(plantPos, state);
            entity.discard();
        }
    }

    private static void smashGourd(ServerWorld world, BlockState state, FallingBlockEntity entity) {
        Item drop = state.isOf(Blocks.MELON) ? ModItems.MASHED_MELON : Items.PUMPKIN_SEEDS;
        onGourdFallDestroyed(world, state, entity.getBoundingBox(), drop, 2);
        entity.discard();
    }

    private static void onGourdFallDestroyed(World world, BlockState state, Box boundingBox, Item drop, int count) {
        Vec3d pos = boundingBox.getCenter();
        BlockPos dropPos = BlockPos.ofFloored(pos);

        // Drops on fall break
        Block.dropStack(world, dropPos, new ItemStack(drop, count));

        world.addBlockBreakParticles(BlockPos.ofFloored(pos), state);
        world.emitGameEvent(ModEvents.GOURD_EXPLODE, dropPos, GameEvent.Emitter.of(state));
        world.playSound(null, dropPos, ModSoundEvents.GOURD_EXPLODE, SoundCategory.BLOCKS, 0.1F,
                0.40F + (world.getRandom().nextFloat() * 0.25F)
        );
    }

}