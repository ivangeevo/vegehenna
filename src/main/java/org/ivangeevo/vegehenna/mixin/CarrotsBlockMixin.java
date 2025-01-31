package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.item.ModItems;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CarrotsBlock.class)
public abstract class CarrotsBlockMixin extends CropBlock implements DailyGrowthCrop {

    // New constants for the AGE property which is now 3 instead of 7;
    @Unique private static final int MAX_AGE = 3;
    @Unique private static final IntProperty AGE = Properties.AGE_3;

    public CarrotsBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedShapes(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(NEW_CARROTS_AGE_TO_SHAPE[this.getAge(state)]);
    }

    @Inject(method = "getSeedsItem", at = @At("HEAD"), cancellable = true)
    private void injectedCustomSeedItem(CallbackInfoReturnable<ItemConvertible> cir) {
        cir.setReturnValue(ModItems.CARROT_SEEDS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE/**, HAS_GROWN_TODAY**/);
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        return false;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }


    /**
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            attemptToGrow(world, pos, state);
        }
    }
     **/

    @Override
    public void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (this.getAge(state) >= this.getMaxAge()) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null ) {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Override
    public float vegehenna$getBaseGrowthChance() {
        return 0.04F;
    }

}
