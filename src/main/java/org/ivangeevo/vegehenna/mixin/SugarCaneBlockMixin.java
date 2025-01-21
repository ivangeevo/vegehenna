package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.block.blocks.OGSugarCaneBlockBase;
import org.ivangeevo.vegehenna.block.blocks.SugarCaneBlockBase;
import org.ivangeevo.vegehenna.util.SugarCaneHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block
{

    @Shadow @Final public static IntProperty AGE;

    public SugarCaneBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        SugarCaneHelper.randomTick(state, world, pos, random);
        ci.cancel();
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void onCanPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(SugarCaneHelper.isAllowedGrowthBlock(world, pos));
    }

}

