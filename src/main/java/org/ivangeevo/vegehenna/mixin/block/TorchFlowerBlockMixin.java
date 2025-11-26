package org.ivangeevo.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.block.interfaces.CropBlockAdded;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TorchflowerBlock.class)
public abstract class TorchFlowerBlockMixin extends CropBlock {

    @Shadow public abstract int getMaxAge();

    public TorchFlowerBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // Copy of the original randomTick method with custom tick logic(renamed to grow)
        // Sets the random to times 4 to effectively make it x4 slower.
        int growthChance = random.nextInt(3);
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock().btwr$getIsFertilizedForPlantGrowth(world, pos.down())) {
            growthChance += (int) belowState.getBlock().btwr$getPlantGrowthOnMultiplier(world, pos.down(), this);
        }

        if (growthChance * 4 != 0) {
            this.grow(state, world, pos, random);
        }

        ci.cancel();
    }

    @Unique
    protected void grow(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i;
        if (world.getBaseLightLevel(pos, 0) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && random.nextInt((int)(25.0f / CropBlock.getAvailableMoisture(this, world, pos)) + 1) == 0) {
            world.setBlockState(pos, this.withAge(i + 1), Block.NOTIFY_LISTENERS);
        }
    }

}