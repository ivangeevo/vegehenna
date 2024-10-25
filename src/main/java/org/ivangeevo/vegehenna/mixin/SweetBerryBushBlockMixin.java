package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin extends PlantBlock
{

    @Shadow @Final public static IntProperty AGE;

    public SweetBerryBushBlockMixin(Settings settings)
    {
        super(settings);
    }

    // Modify the drop to only 1 instead of a random like before.
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void injectedOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        boolean bl;
        int i = state.get(AGE);
        bl = i == 3;

        Hand hand = player.getActiveHand();

        if (!bl && player.getStackInHand(hand).isOf(Items.BONE_MEAL))
        {
            cir.setReturnValue( ActionResult.PASS );
        }

        if (i > 1)
        {
            SweetBerryBushBlock.dropStack(world, pos, new ItemStack(Items.SWEET_BERRIES, 1));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
            BlockState blockState = state.with(AGE, 1);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
            cir.setReturnValue( ActionResult.success(world.isClient) );
        }
        cir.setReturnValue( super.onUse(state, world, pos, player, hit) );
    }
}
