package org.ivangeevo.vegehenna.mixin.block;

import btwr.btwr_sl.tag.BTWRConventionalTags;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.ivangeevo.vegehenna.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract BlockState getBlockState();


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;onDestroyedOnLanding(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
    private void onBeforeLanding(CallbackInfo ci) {
        if (this.getBlockState().isOf(Blocks.MELON)) {
            onGourdFallDestroyed(ModItems.MASHED_MELON, 2);
        }

        if (this.getBlockState().isOf(Blocks.PUMPKIN)) {
            onGourdFallDestroyed(Items.PUMPKIN_SEEDS, 4);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;isOnGround()Z"))
    private void checkUnsafeGourdLanding(CallbackInfo ci) {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        BlockState landing = world.getBlockState(pos);
        BlockState below = world.getBlockState(pos.down());

        if (isGourdBlock(this.getBlockState())) {
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
    private void onGourdFallDestroyed(Item drop, int count) {
        World world = this.getWorld();
        Vec3d pos = this.getBoundingBox().getCenter();
        BlockPos dropPos = BlockPos.ofFloored(pos);

        // Drops on fall break
        Block.dropStack(world, dropPos, new ItemStack(drop, count));

        // Play effects
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, dropPos, Block.getRawIdFromState(this.getBlockState()));
        // Playing a sound instead of the block destroy event to have a custom sound
        world.playSound(null, dropPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,
                SoundCategory.BLOCKS, 0.1F, 0.40F + (world.getRandom().nextFloat() * 0.25F)
        );
        //world.emitGameEvent(this, GameEvent.BLOCK_DESTROY, pos);

    }

    @Unique
    private boolean isGourdBlock(BlockState state) {
        return state.isOf(Blocks.MELON) || state.isOf(Blocks.PUMPKIN);
    }
}
