package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BeetrootsBlock.class)
public abstract class BeetrootsBlockMixin extends CropBlock implements DailyGrowthCrop {

    @Shadow @Final public static IntProperty AGE;

    public BeetrootsBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(NEW_BEETS_AGE_TO_SHAPE[this.getAge(state)]);
    }

    //@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            this.vegehenna$attemptToGrow(world, pos, state, random);
        }

        ci.cancel();
    }

    //@Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }

    @Override
    public int getAge(BlockState state) { return state.get(this.getAgeProperty()); }

    @Override
    public IntProperty getAgeProperty() { return AGE; }


    @Override
    public void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (iGrowthLevel >= 3) {
            BlockState belowState = world.getBlockState(pos.down());

            if ( belowState != null ) {
                belowState.getBlock().notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }
        }
    }
}
