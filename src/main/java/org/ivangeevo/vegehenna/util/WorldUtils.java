// FCMOD

package org.ivangeevo.vegehenna.util;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Helper functions related to the world
 */
public class WorldUtils
{
    public static boolean isReplaceableBlock(BlockState state)
    {
        return state.getBlock() == null || state.isReplaceable();
    }



}