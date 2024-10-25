// FCMOD

package org.ivangeevo.vegehenna.util;


import net.minecraft.block.BlockState;

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