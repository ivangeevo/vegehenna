package org.ivangeevo.vegehenna.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class UncookedCakeBlock extends Block {

    private static final VoxelShape CAKE_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 8, 15);

    public UncookedCakeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CAKE_SHAPE;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            dropBlockAsItem(world, pos);
            world.removeBlock(pos, false);
        }

        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    private void dropBlockAsItem(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ModBlocks.UNCOOKED_CAKE.asItem().getDefaultStack());
    }
}
