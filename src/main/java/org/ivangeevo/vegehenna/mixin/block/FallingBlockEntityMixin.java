package org.ivangeevo.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.util.falling_blocks.FallingGourdBEHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        FallingGourdBEHandler.getInstance().onBeforeLanding(this.getWorld(), this.getBlockState(), this.getBoundingBox());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;isOnGround()Z"))
    private void checkUnsafeGourdLanding(CallbackInfo ci) {
        FallingGourdBEHandler.getInstance().checkUnsafeGourdLanding(this.getWorld(), this.getBlockPos(), this.getBlockState());
    }

}
