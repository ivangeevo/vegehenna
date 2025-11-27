package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.btwr.shared_library.tag.BTWRConventionalTags;
import org.btwr.vegehenna.block.interfaces.CropBlockAdded;
import org.btwr.vegehenna.block.interfaces.DailyGrowthCrop;
import org.btwr.vegehenna.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements CropBlockAdded, Fertilizable, DailyGrowthCrop {

    @Shadow public abstract int getAge(BlockState state);
    @Shadow protected abstract IntProperty getAgeProperty();
    @Shadow public abstract int getMaxAge();

    @Unique private boolean isDailyGrowthCrop = false;

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Settings settings, CallbackInfo ci) {
        if (this.getDefaultState().isIn(ModTags.Blocks.DAILY_GROWTH_CROPS)) {
            this.isDailyGrowthCrop = true;
            this.setDefaultState(
                    this.getStateManager().getDefaultState()
                            .with(this.getAgeProperty(), 0)
                            .with(HAS_GROWN_TODAY, false)
            );
        }
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (this.isDailyGrowthCrop) {
            builder.add(HAS_GROWN_TODAY);
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if ((CropBlock)(Object)this instanceof TorchflowerBlock) return;
        // TODO: Move the dimension check for crops only for the modpack?. idk
        if (!(world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID)) && state.isOf(this)) {
            if (state.getBlock() instanceof DailyGrowthCrop) {
                attemptToGrow(world, pos, state);
            }
        }

        ci.cancel();
    }

    // Custom outline shape
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(NEW_DEFAULT_AGE_TO_SHAPE[this.getAge(state)]);
    }

    // Make it not fertilizable by the traditional way
    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    private void injectedIsFertilizable(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
       cir.setReturnValue(floor.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS) || floor.isOf(Blocks.FARMLAND));
    }


    @Override
    public void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(this.getAgeProperty(), iGrowthLevel),2);

        if (iGrowthLevel >= this.getMaxAge()) {
            BlockState belowState = world.getBlockState(pos.down());

            if (belowState != null) {
                belowState.getBlock().btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }
        }
    }

    // This is the base growth chance for all blocks that use the CropBlock
    // Same value as in the original DailyGrowthCropsBlock class from BTW
    @Override
    public float vegehenna$getBaseGrowthChance() {
        return 0.04F;
    }

    @Override
    public int vegehenna$getLightLevelForGrowth() {
        return 9;
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        BlockState state = this.getDefaultState();
        return state.isOf(Blocks.WHEAT);
    }

}