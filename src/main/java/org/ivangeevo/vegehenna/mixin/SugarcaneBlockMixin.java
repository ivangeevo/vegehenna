package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.tag.ModTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.x;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin extends Block
{

    @Shadow @Final public static IntProperty AGE;

    public SugarcaneBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void injectedTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
    {
        // prevent growth in the end dimension
        if (world.getDimensionKey() != DimensionTypes.THE_END && this.canPlaceAt(state, world, pos))
        {
            if (world.isAir(pos.up())) {
                int reedHeight = 1;

                BlockPos checkPos = new BlockPos(pos.getX(), pos.getY() - reedHeight, pos.getZ());

                while (world.getBlockState(checkPos).getBlock() instanceof SugarCaneBlock)
                {
                    reedHeight++;
                }

                if (reedHeight < 3)
                {

                    if (state.get(AGE) == 15)
                    {
                        world.setBlockState(pos.up(), Blocks.SUGAR_CANE.getDefaultState());

                        world.setBlockState(pos, state.with(AGE, 0), 4 );
                    }
                    else
                    {
                        world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), 4);
                    }

                }
            }
        }
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void injectedCanPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (world.getBlockState(pos).getBlock() == Blocks.WATER)
        {
            cir.setReturnValue(false);

        }

        BlockState stateBelow = world.getBlockState(pos.down());
        Block blockBelow = stateBelow.getBlock();

        cir.setReturnValue( blockBelow == Blocks.SUGAR_CANE || (blockBelow != null && world.getBlockState(pos.down()).isIn(ModTags.Blocks.REEDS_CAN_GROW_ON) &&
                isConsideredNeighbouringWaterForReedGrowthOn(world, pos.down())) );

    }

    @Unique
    public boolean isConsideredNeighbouringWaterForReedGrowthOn(WorldView world, BlockPos pos)
    {
        for ( int iTempI = pos.getX() - 1; iTempI <= pos.getX() + 1; iTempI++ )
        {
            for ( int iTempK = pos.getZ() - 1; iTempK <= pos.getZ() + 1; iTempK++ )
            {
                BlockPos tempPos = new BlockPos(iTempI, pos.getY(), iTempK);
                if ( world.getBlockState(tempPos).getBlock() == Blocks.WATER)
                {
                    return true;
                }
            }
        }

        return false;
    }






}
