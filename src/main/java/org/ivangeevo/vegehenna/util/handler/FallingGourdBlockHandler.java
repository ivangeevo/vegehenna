package org.ivangeevo.vegehenna.util.handler;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.vegehenna.util.falling_blocks.FallingBlockSupport;
import org.ivangeevo.vegehenna.util.api.FallingBlockAPI;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FallingGourdBlockHandler {

    private static final FallingGourdBlockHandler INSTANCE = new FallingGourdBlockHandler();

    private FallingGourdBlockHandler() {}

    public static FallingGourdBlockHandler getInstance() {
        return INSTANCE;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos) {
        if (FallingBlockAPI.isFallingLike(state)) {
            boolean safeSupport = FallingBlockSupport.isSolidTop(world, pos.down(), state, Direction.UP);
            int delay = safeSupport ? 10 : 2;
            world.scheduleBlockTick(pos, state.getBlock(), delay);
        }
    }

    public void getStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos pos) {
        if (FallingBlockAPI.isFallingLike(state)) {
            boolean safeSupport = FallingBlockSupport.isSolidTop(world, pos.down(), state, Direction.UP);
            int delay = safeSupport ? 10 : 2;
            world.scheduleBlockTick(pos, state.getBlock(), delay);
        }
    }

    public void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (FallingBlockAPI.isFallingLike(state)) {
            BlockPos below = pos.down();
            BlockState belowState = world.getBlockState(below);

            if (FallingBlock.canFallThrough(belowState) && pos.getY() >= world.getBottomY() || belowState.getCollisionShape(world, below).getMax(Direction.Axis.Y) < 1.0) {
                FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, state);

                // Call custom handler if one exists
                FallingBlockAPI.applyFallHandler(world, pos, state, entity);

                ci.cancel();
            }
        }
    }

}