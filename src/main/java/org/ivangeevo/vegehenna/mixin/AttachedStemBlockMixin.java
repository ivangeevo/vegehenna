package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AttachedStemBlock.class)
public abstract class AttachedStemBlockMixin extends PlantBlock {

    protected AttachedStemBlockMixin(Settings settings) {
        super(settings);
    }

    // Revert to an earlier AGE when its associated GourdBlock is harvested.
    @ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 7))
    private int injected(int value) {
        return 4;
    }
}
