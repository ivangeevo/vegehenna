package org.btwr.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.btwr.vegehenna.util.api.FallingBlockAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow public abstract BlockState getBlockState();

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), cancellable = true)
    private void onBeforePlacement(CallbackInfo ci) {
        if (this.getWorld().isClient) return;
        BlockState state = this.getBlockState();
        if (!FallingBlockAPI.hasLandHandler(state)) return;

        FallingBlockEntity self = (FallingBlockEntity)(Object) this;
        int blocksFallen = self.getFallingBlockPos().getY() - this.getBlockPos().getY();

        FallingBlockAPI.applyLandHandler(
                (ServerWorld) this.getWorld(),
                this.getBlockPos(),
                state,
                self,
                blocksFallen
        );
        ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;onDestroyedOnLanding(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void onDestroyedOnLandingInject(CallbackInfo ci) {
        if (this.getWorld().isClient) return;
        BlockState state = this.getBlockState();
        if (!FallingBlockAPI.hasLandHandler(state)) return;

        FallingBlockEntity self = (FallingBlockEntity)(Object) this;
        int blocksFallen = self.getFallingBlockPos().getY() - this.getBlockPos().getY();

        FallingBlockAPI.applyLandHandler(
                (ServerWorld) this.getWorld(),
                this.getBlockPos(),
                state,
                self,
                blocksFallen
        );
        ci.cancel();
    }

}