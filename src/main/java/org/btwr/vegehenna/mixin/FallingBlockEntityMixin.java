package org.btwr.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.World;
import org.btwr.vegehenna.util.falling_blocks.FallingBlockEntityHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow public abstract BlockState getBlockState();

    @Unique private final FallingBlockEntityHandler handler = FallingBlockEntityHandler.getInstance();

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;onDestroyedOnLanding(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
    private void onBeforeLanding(CallbackInfo ci) {
        handler.beforeDestroyedOnLanding(this.getWorld(), this.getBlockState(), this.getBoundingBox());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;isOnGround()Z"))
    private void checkUnsafeGourdLanding(CallbackInfo ci) {
        FallingBlockEntityHandler.getInstance().checkUnsafeGourdLanding(this.getWorld(), this.getBlockPos(), this.getBlockState());
    }

}