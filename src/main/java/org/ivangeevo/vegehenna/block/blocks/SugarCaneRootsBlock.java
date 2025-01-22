package org.ivangeevo.vegehenna.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.ivangeevo.vegehenna.tag.ModTags;
import org.ivangeevo.vegehenna.util.SugarCaneHelper;

public class SugarCaneRootsBlock extends Block
{

    public static final MapCodec<SugarCaneRootsBlock> CODEC = Block.createCodec(SugarCaneRootsBlock::new);

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public SugarCaneRootsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        SugarCaneHelper.appendProperties(builder);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SugarCaneHelper.SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        SugarCaneHelper.randomTick(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        SugarCaneHelper.scheduledTick(world, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        return blockBelow != null &&
                !(blockBelow instanceof SugarCaneBlock) &&
                baseCanPlaceAt(world, pos) && blockBelow != this;
    }


     @Override
     public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean baseCanPlaceAt(WorldView world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
            return false;
        }

        BlockState stateBelow = world.getBlockState(pos.down());
        Block blockBelow = stateBelow.getBlock();

        return blockBelow == this.asBlock() || (blockBelow != null && stateBelow.isIn(ModTags.Blocks.REEDS_CAN_PLANT_ON) &&
                SugarCaneHelper.isConsideredNeighbouringWaterForReedGrowthOn(world, pos.down()));
    }
}
