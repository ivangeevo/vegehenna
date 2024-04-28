package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin extends Block
{

    @Shadow @Final public static IntProperty MOISTURE;

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

}
