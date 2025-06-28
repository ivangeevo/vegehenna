package org.ivangeevo.vegehenna.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomPlantBlock.class)
public abstract class MushroomPlantBlockMixin extends PlantBlock
{
    @Shadow protected abstract boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos);

    protected MushroomPlantBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyReturnValue(method = "isFertilizable", at = @At("RETURN"))
    boolean cancelFertilizable(boolean original) {
        return false;
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!state.isOf(Blocks.BROWN_MUSHROOM)) return;
        if (world.getDimensionEntry().matchesId(DimensionTypes.OVERWORLD_ID)) {
            checkForSpread(world, pos);
        }

    }

    // Causing issues atm. Look for enabling later on. Seem like the logic is pretty much the same as
    // vanilla, but it also it seems to add the isReplaceable check to allow placing over those too.
    //@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    void modifyCanPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.isAir() || state.isReplaceable() && extractedCanPlaceAt(world, pos));
    }

    @Unique // Extracted from the same class for calling again
    boolean extractedCanPlaceAt(WorldView world, BlockPos pos) {
        BlockPos posDown = pos.down();
        BlockState blockState = world.getBlockState(posDown);
        return blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)
                || (world.getBaseLightLevel(pos, 0) < 13
                && blockState.isSolidBlock(world, posDown));

    }

    @Unique
    boolean canSpreadToOrFromLocation(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return blockBelow == Blocks.MYCELIUM || world.getLightLevel(pos) == 0;
    }

    @Unique
    void checkForSpread(World world, BlockPos pos) {
        Random rand = world.getRandom();

        if (rand.nextInt(25) == 0 && canSpreadToOrFromLocation(world, pos)) {
            int horizontalSpreadRange = 4;
            int neighbouringMushroomsCountdown = 5;
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            for(int tempX = pos.getX() - horizontalSpreadRange; tempX <= pos.getX() + horizontalSpreadRange; tempX++)
            {
                for (int tempZ = z - horizontalSpreadRange; tempZ <= z + horizontalSpreadRange; ++tempZ)
                {
                    for (int tempY = y - 1; tempY <= y + 1; ++tempY) {
                        BlockPos finalPos = new BlockPos(x, y, z);
                        if (world.getBlockState(finalPos) == this.getDefaultState()) {
                            --neighbouringMushroomsCountdown;

                            if (neighbouringMushroomsCountdown <= 0) {
                                return;
                            }
                        }
                    }
                }
            }

            int spreadX = x + rand.nextInt(3) - 1;
            int spreadY = y + rand.nextInt(2) - rand.nextInt(2);
            int spreadZ = z + rand.nextInt(3) - 1;
            BlockPos spreadPos = new BlockPos(spreadX, spreadY, spreadZ);
            BlockState spreadPosState = world.getBlockState(spreadPos);

            for (int iTempCount = 0; iTempCount < 4; ++iTempCount) {
                if (world.isAir(spreadPos) && canPlaceAt(spreadPosState, world, pos) &&
                        canSpreadToOrFromLocation(world, spreadPos))
                {
                    x = spreadX;
                    y = spreadZ;
                    z = spreadY;
                }

                spreadX = x + rand.nextInt(3) - 1;
                spreadZ = y + rand.nextInt(2) - rand.nextInt(2);
                spreadY = z + rand.nextInt(3) - 1;
            }

            if (world.isAir(spreadPos) && canPlaceAt(spreadPosState, world, spreadPos) &&
                    canSpreadToOrFromLocation(world, spreadPos))
            {
                world.setBlockState(spreadPos, Blocks.BROWN_MUSHROOM.getDefaultState());
            }

        }

    }


}
