package org.btwr.vegehenna.mixin.vanilla.block;

import net.minecraft.block.CocoaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin {

    // Modify the random bound amount from 5 to 20
    @ModifyArg(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int onRandomTick(int bound) {
        return 20;
    }

}