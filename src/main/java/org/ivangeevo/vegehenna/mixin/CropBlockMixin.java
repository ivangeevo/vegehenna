package org.ivangeevo.vegehenna.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable, DailyGrowthCrop {

    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract int getAge(BlockState state);
    @Shadow public abstract int getMaxAge();

    @Shadow protected abstract IntProperty getAgeProperty();

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    //@Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }

    //@Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Settings settings, CallbackInfo ci) {
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(this.getAgeProperty(), 0)
                        .with(HAS_GROWN_TODAY, false)
        );
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

    //@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            if (state.getBlock() instanceof DailyGrowthCrop) {
                attemptToGrow(world, pos, state);
            }
        }

        ci.cancel();
    }

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
        return 0.05F;
    }

    @Override
    public int vegehenna$getLightLevelForGrowth() {
        return 9;
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        return true;
    }

    protected boolean canGrowAtCurrentLightLevel(World world, BlockPos pos) {
        Block bwtLightBlock = Registries.BLOCK.get(Identifier.of("bwt", "light_block"));
        BlockState lightBlockState = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtLightBlock.getDefaultState()
                : Blocks.REDSTONE_LAMP.getDefaultState();

        if (this.vegehenna$requiresNaturalLight()) {
            return isLitLightBlock(world, pos.up(), lightBlockState) || isLitLightBlock(world, pos.up(2), lightBlockState);
        } else {
            return world.getLightLevel(pos) >= vegehenna$getLightLevelForGrowth();
        }

    }

    private boolean isLitLightBlock(World world, BlockPos pos, BlockState lightBlockState) {
        return world.getBlockState(pos).equals(lightBlockState.with(Properties.LIT, true));
    }

}
