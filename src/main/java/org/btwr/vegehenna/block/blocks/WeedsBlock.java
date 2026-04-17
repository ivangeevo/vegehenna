package org.btwr.vegehenna.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;


public class WeedsBlock extends PlantBlock {
    public static final MapCodec<WeedsBlock> CODEC = createCodec(WeedsBlock::new);
    @Override protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    public static final int LIGHT_LEVEL_FOR_WEED_GROWTH = 11;
    public static final long NIGHT_START = 14000L;
    public static final long NIGHT_END = 22000L;

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 3);

    static public final VoxelShape SHAPE = Block.createCuboidShape(
            2, 0, 2, 14, 8, 14
    );

    public WeedsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(LEVEL, 0));
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int level = state.get(LEVEL);

        if (world.getRandom().nextInt(20) != 0) return;

        if (level < 3) {
            world.setBlockState(pos, state.with(LEVEL, level + 1));
        } else {
            world.setBlockState(pos, Blocks.SHORT_GRASS.getDefaultState());
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            breakWeeds(world, pos, state, player, false);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hit);
    }

    public static void breakWeeds(World world, BlockPos pos, BlockState state, PlayerEntity player, boolean hasCrop) {
        // Break particles + sound
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));

        // Remove the block
        if (!hasCrop) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
    }
}