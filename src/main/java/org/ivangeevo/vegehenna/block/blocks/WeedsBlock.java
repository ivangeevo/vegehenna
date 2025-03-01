package org.ivangeevo.vegehenna.block.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.function.Function;

public class WeedsBlock extends PlantBlock
{
    public static final MapCodec<WeedsBlock> CODEC = createCodec(WeedsBlock::new);

    public static final IntProperty WEEDS_LEVEL = IntProperty.of("weeds_level", 0, 3);


    static public final double WEEDS_BOUNDS_WIDTH = (1D - (4D / 16D));
    static public final double WEEDS_BOUNDS_HALF_WIDTH = (WEEDS_BOUNDS_WIDTH / 2D);
    static protected final VoxelShape SHAPE = createCuboidShape(
            0.5D - WEEDS_BOUNDS_WIDTH, 0D, 0.5D - WEEDS_BOUNDS_WIDTH,
            0.5D + WEEDS_BOUNDS_WIDTH, 0.5D, 0.5D + WEEDS_BOUNDS_WIDTH);



    public WeedsBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WEEDS_LEVEL);
    }

    /**
    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        float dVerticalOffset = 0;
        BlockState blockBelow = world.getBlockState(pos.down());

        if (blockBelow != null) {
            dVerticalOffset = blockBelow.getBlock().getVisualOffset(world, pos.down());
        }

        int iGrowthLevel = getWeedsGrowthLevel(world, pos);

        double dBoundsHeight = getWeedsBoundsHeight(iGrowthLevel);

        double minX = 0.5D - WEEDS_BOUNDS_HALF_WIDTH;
        double minY = 0F + dVerticalOffset;
        double minZ = 0.5D - WEEDS_BOUNDS_HALF_WIDTH;
        double maxX = 0.5D + WEEDS_BOUNDS_HALF_WIDTH;
        double maxY = dBoundsHeight + dVerticalOffset;
        double maxZ = 0.5D + WEEDS_BOUNDS_HALF_WIDTH;

        // Convert the bounding box to a VoxelShape
        VoxelShape shape = VoxelShapes.cuboid(minX, minY, minZ, maxX, maxY, maxZ);

        // Return a single-state mapping
        return ImmutableMap.of(state, shape);
    }
    **/

}
