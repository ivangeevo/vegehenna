package org.ivangeevo.vegehenna.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.CropBlockAdded;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable, CropBlockAdded {

    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract int getAge(BlockState state);
    @Shadow public abstract int getMaxAge();

    @Unique
    private static final BooleanProperty HAS_GROWN_TODAY = BooleanProperty.of("has_grown_today");

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    // Making all crops to not drop their stacks when broken by being stepped-on
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onSteppedOn(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, false, entity);
        }
        super.onEntityCollision(state, world, pos, entity);

        ci.cancel();
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }

    // Custom outline shape
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( NEW_AGE_TO_SHAPE[this.getAge(state)] );
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

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
    {

        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this))
        {
            attemptToGrow(world, pos, state, random);
        }

        ci.cancel() ;
    }

    @Override
    public void attemptToGrow(World world, BlockPos pos, BlockState state, Random rand) {

        /**
        if (getWeedsGrowthLevel(world, pos) == 0 && getGrowthLevel(world, pos) < this.getMaxAge() && world.getLightLevel( pos.up() ) >= 9 )
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null && (blockBelow.isBlockHydratedForPlantGrowthOn(world, pos.down()))) {
                float fGrowthChance = getBaseGrowthChance() *
                        (blockBelow.getPlantGrowthOnMultiplier(world, pos.down(), this));

                if ( rand.nextFloat() <= fGrowthChance ) {
                    incrementGrowthLevel(world, pos, state);
                    //updateFlagForGrownToday(world, pos);

                }
            }
        }
         **/

        int timeOfDay = (int)(world.getTime() % 24000L);

        if (timeOfDay > 14000 && timeOfDay < 22000) {
            // night
            if (state.get(HAS_GROWN_TODAY)) {
                setHasGrownToday(world, pos, false);
            }
        }
        else {
            if (!state.get(HAS_GROWN_TODAY) && getWeedsGrowthLevel(world, pos) == 0 && canGrowAtCurrentLightLevel(world, pos)) {
                Block blockBelow = world.getBlockState(pos.down()).getBlock();

                if (blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, pos.down())) {
                    float growthChance = getBaseGrowthChance();

                    if (blockBelow.getIsFertilizedForPlantGrowth(world, pos.down())) {
                        growthChance *= 2F;
                    }

                    if (rand.nextFloat() <= growthChance) {
                        incrementGrowthLevel(world, pos, state);
                        updateFlagForGrownToday(world, pos);
                    }
                }
            }
        }

    }

    protected void updateFlagForGrownToday(World world, BlockPos pos) {

        // fertilized crops can grow twice in a day
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        if ( blockBelow != null ) {
            if (!blockBelow.getIsFertilizedForPlantGrowth(world, pos.down()) ||
                    getGrowthLevel(world, pos) % 2 == 0 )
            {
                setHasGrownToday(world, pos, true);
            }
        }
    }

    // Sets whether the block has grown today
    public void setHasGrownToday(World world, BlockPos pos, boolean hasGrown) {
        BlockState currentState = world.getBlockState(pos);
        BlockState newState = currentState.with(HAS_GROWN_TODAY, hasGrown);

        // No notify flag is equivalent to passing '2' for flags to suppress visual updates and block updates
        world.setBlockState(pos, newState, 2);
    }

    @Override
    public void incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (getGrowthLevel(world,pos) >= this.getMaxAge())
        {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null )
            {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Override
    public float getBaseGrowthChance()
    {
        return 0.05F;
    }

    @Override
    public int getGrowthLevel(WorldAccess blockAccess, BlockPos pos) {
        return this.getAge(blockAccess.getBlockState(pos));
    }

    @Override
    public int getLightLevelForGrowth() {
        return 9;
    }

    @Override
    public boolean requiresNaturalLight() {
        return true;
    }

    @Unique
    private static final VoxelShape[] NEW_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 9.0, 14.0)
    };

    protected boolean canGrowAtCurrentLightLevel(World world, BlockPos pos) {
        Block bwtLightBlock = Registries.BLOCK.get(Identifier.of("bwt", "light_block"));
        BlockState lightBlockState = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtLightBlock.getDefaultState()
                : Blocks.REDSTONE_LAMP.getDefaultState();

        if (this.requiresNaturalLight()) {
            return isLitLightBlock(world, pos.up(), lightBlockState) || isLitLightBlock(world, pos.up(2), lightBlockState);
        } else {
            return world.getLightLevel(pos) >= getLightLevelForGrowth();
        }

    }

    private boolean isLitLightBlock(World world, BlockPos pos, BlockState lightBlockState) {
        return world.getBlockState(pos).equals(lightBlockState.with(Properties.LIT, true));
    }

}
