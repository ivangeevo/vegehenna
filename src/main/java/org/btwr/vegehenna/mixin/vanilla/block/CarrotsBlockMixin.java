package org.btwr.vegehenna.mixin.vanilla.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.btwr.vegehenna.item.ModItems;
import org.btwr.vegehenna.block.interfaces.DailyGrowthCrop;
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
        if (world.getBlockEntity(pos.down()) instanceof WeedsBlockEntity weedsBE) {
            if (weedsBE.getLevel() >= 1) {
                cir.setReturnValue(WeedsBlock.SHAPE);
                return;
            }
        }
        cir.setReturnValue(NEW_CARROTS_AGE_TO_SHAPE[this.getAge(state)]);
    }

    @Inject(method = "getSeedsItem", at = @At("HEAD"), cancellable = true)
    private void injectedCustomSeedItem(CallbackInfoReturnable<ItemConvertible> cir) {
        cir.setReturnValue(ModItems.CARROT_SEEDS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, HAS_GROWN_TODAY);
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public float vegehenna$getBaseGrowthChance() {
        return 0.04F;
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        return false;
    }

}