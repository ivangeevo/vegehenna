package org.btwr.vegehenna.mixin.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.vegehenna.block.ModBlocks;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.FarmlandBlock.MOISTURE;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block implements BlockEntityProvider {

    @Unique private static final int LIGHT_LEVEL_FOR_WEED_GROWTH = 11;
    @Unique private static final long NIGHT_START = 14000L;
    @Unique private static final long NIGHT_END = 22000L;

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) {
            return new WeedsBlockEntity(pos, state);
        }
        return null;
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        updateWeedGrowth(world, pos);
    }

    @Override
    public boolean btwr$isBlockHydratedForPlantGrowthOn(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.contains(MOISTURE) && state.get(MOISTURE) > 0;
    }

    @Unique
    private void updateWeedGrowth(ServerWorld world, BlockPos pos) {
        if (!world.getBlockState(pos).isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)) return;

        BlockPos cropPos = pos.up();
        BlockState aboveState = world.getBlockState(cropPos);

        if (!(world.getBlockEntity(pos) instanceof WeedsBlockEntity weedsBE)) return;

        int weedLevel = weedsBE.getLevel();
        long timeOfDay = world.getTimeOfDay() % 24000L;
        boolean isNight = timeOfDay > NIGHT_START && timeOfDay < NIGHT_END;

        if (aboveState.isAir()) {
            if (world.getRandom().nextInt(20) == 0) {
                world.setBlockState(cropPos, ModBlocks.WEEDS.getDefaultState());
            }
        } else if (canWeedsShareSpaceWith(aboveState)) {
            updateForCropPresent(world, cropPos, weedsBE, isNight, weedLevel);
        } else if (weedLevel > 0) {
            weedsBE.removeWeeds();
        }
    }

    @Unique
    private void updateForCropPresent(ServerWorld world, BlockPos pos, WeedsBlockEntity weedsBE, boolean isNight, int weedLevel) {
        if (isNight) {
            if (weedLevel == 0) {
                if (world.getRandom().nextInt(20) == 0
                        && world.getLightLevel(LightType.SKY, pos) >= LIGHT_LEVEL_FOR_WEED_GROWTH) {
                    weedsBE.setLevel(1);
                }
            } else if (weedLevel % 2 == 0) {
                weedsBE.setLevel(weedLevel + 1);
            }
        } else {
            if (world.getLightLevel(LightType.SKY, pos) >= LIGHT_LEVEL_FOR_WEED_GROWTH) {
                if (weedLevel == 7) {
                    weedsBE.removeWeeds();
                    world.setBlockState(pos, Blocks.SHORT_GRASS.getDefaultState());
                } else if (weedLevel % 2 == 1) {
                    weedsBE.setLevel(weedLevel + 1);
                }
            }
        }
    }

    @Unique
    private boolean canWeedsShareSpaceWith(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof AttachedStemBlock;
    }

}