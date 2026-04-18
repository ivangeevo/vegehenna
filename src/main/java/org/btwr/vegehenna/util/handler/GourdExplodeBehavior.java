package org.btwr.vegehenna.util.handler;

import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
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

public class GourdExplodeBehavior {

    private static final List<Block> GOURD_BLOCKS = List.of(Blocks.MELON, Blocks.PUMPKIN);

    public static void register() {
        for (var entry : GOURD_BLOCKS) {
            FallingBlockAPI.registerFallingBlock(entry, ((world, pos, state, entity) -> {}));
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
        explode(world, state, entity.getBoundingBox(), drop);
        entity.discard();
    }

    public static void explode(World world, BlockState state, Box boundingBox, Item drop) {
        Vec3d pos = boundingBox.getCenter();
        BlockPos dropPos = BlockPos.ofFloored(pos);

        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 150; i++) {
                double particleX = dropPos.getX() + world.getRandom().nextDouble() - 0.5D;
                double particleY = dropPos.getY() - 0.45D;
                double particleZ = dropPos.getZ() + world.getRandom().nextDouble() - 0.5D;

                double particleVelX = (world.getRandom().nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.getRandom().nextDouble() * 0.7D;
                double particleVelZ = (world.getRandom().nextDouble() - 0.5D) * 0.5D;

                serverWorld.spawnParticles(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(getGourdParticleItem(state))),
                        particleX, particleY, particleZ,
                        1, // count — set to 1 since we're looping manually
                        particleVelX, particleVelY, particleVelZ,
                        0 // speed
                );
            }
        }

        world.emitGameEvent(ModEvents.GOURD_EXPLODE, dropPos, GameEvent.Emitter.of(state));
        world.playSound(null, dropPos, ModSoundEvents.GOURD_EXPLODE, SoundCategory.BLOCKS,
                0.2F, 0.60F + (world.getRandom().nextFloat() * 0.25F)
        );
        world.playSound(null, dropPos, ModSoundEvents.GOURD_EXPLODE_LAYER, SoundCategory.BLOCKS,
                1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 0.6F
        );

        Block.dropStack(world, dropPos, new ItemStack(drop, getGourdDropItemCount(state)));
    }

    public static void onProjectileHit(World world, BlockState state, BlockPos blockPos) {
        world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        explode(world, state, new Box(blockPos), getGourdDrop(state));
    }

    public static void onProjectileWeakHit(World world, BlockPos blockPos) {
        world.playSound(null, blockPos, ModSoundEvents.GOURD_IMPACT, SoundCategory.BLOCKS,
                0.1F, 0.40F + (world.getRandom().nextFloat() * 0.25F));
    }

    private static Item getGourdDrop(BlockState state) {
        if (state.isOf(Blocks.MELON)) return ModItems.MASHED_MELON;
        if (state.isOf(Blocks.PUMPKIN)) return Items.PUMPKIN_SEEDS;
        return ModItems.MASHED_MELON;
    }

    private static Item getGourdParticleItem(BlockState state) {
        if (state.isOf(Blocks.MELON)) return ModItems.MASHED_MELON;
        if (state.isOf(Blocks.PUMPKIN)) return ModItems.COOKED_CARROT;
        return ModItems.MASHED_MELON;
    }

    private static int getGourdDropItemCount(BlockState state) {
        if (state.isOf(Blocks.MELON)) return 2;
        if (state.isOf(Blocks.PUMPKIN)) return 4;
        return 2;
    }

}