package org.btwr.vegehenna.util.falling_blocks;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.btwr.shared_library.tag.BTWRConventionalTags;
import org.btwr.vegehenna.item.ModItems;
import org.btwr.vegehenna.tag.ModTags;
import org.spongepowered.asm.mixin.Unique;

public class FallingGourdBEHandler {

    private static FallingGourdBEHandler INSTANCE = new FallingGourdBEHandler();

    private FallingGourdBEHandler() {}

    public static FallingGourdBEHandler getInstance() {
        return INSTANCE;
    }

    public void onBeforeLanding(World world, BlockState state, Box boundingBox) {
        if (state.isOf(Blocks.MELON)) {
            onGourdFallDestroyed(world, state, boundingBox, ModItems.MASHED_MELON, 2);
        }

        if (state.isOf(Blocks.PUMPKIN)) {
            onGourdFallDestroyed(world, state, boundingBox, Items.PUMPKIN_SEEDS, 4);
        }
    }

    public void checkUnsafeGourdLanding(World world, BlockPos pos, BlockState state) {
        BlockState landing = world.getBlockState(pos);
        BlockState below = world.getBlockState(pos.down());

        if (state.isIn(ModTags.Blocks.GOURD_BLOCKS)) {
            boolean unsafeSurface = FallingBlock.canFallThrough(below) || landing.getBlock() instanceof PlantBlock;

            if (unsafeSurface || below.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
                if (below.isOf(Blocks.FARMLAND)) {
                    world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState());
                }
                // optionally destroy
                // this.destroyedOnLanding = true;
            }
        }
    }

    @Unique
    private void onGourdFallDestroyed(World world, BlockState state, Box boundingBox, Item drop, int count) {
        Vec3d pos = boundingBox.getCenter();
        BlockPos dropPos = BlockPos.ofFloored(pos);

        // Drops on fall break
        Block.dropStack(world, dropPos, new ItemStack(drop, count));

        // Play effects
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, dropPos, Block.getRawIdFromState(state));
        // Playing a sound instead of the block destroy event to have a custom sound
        world.playSound(null, dropPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,
                SoundCategory.BLOCKS, 0.1F, 0.40F + (world.getRandom().nextFloat() * 0.25F)
        );
        //world.emitGameEvent(this, GameEvent.BLOCK_DESTROY, pos);

    }



}
