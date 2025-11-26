package org.ivangeevo.vegehenna.mixin.block;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.shared_library.tag.BTWRConventionalTags;
import org.btwr.shared_library.util.utils.IdUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends Block
{

    public PlantBlockMixin(Settings settings) {
        super(settings);
    }

    // Adds an item tag check for what blocks plants can be planted on.
    // Takes into account if BWT is loaded to prioritize it's own farmland tag, or else use the BTWR Shared lib one.
    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    private void setFarmlandBlocksAsViable(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        Identifier bwtFarmlandTagID = IdUtils.ofBWT("farmland");
        TagKey<Block> bwtFarmlandTag = TagKey.of(RegistryKeys.BLOCK, bwtFarmlandTagID);
        TagKey<Block> conditional = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtFarmlandTag
                : BTWRConventionalTags.Blocks.FARMLAND_BLOCKS;
        cir.setReturnValue(floor.isIn(BlockTags.DIRT) || floor.isOf(Blocks.FARMLAND) || floor.isIn(conditional));
    }

    @Override
    public int btwr$getWeedsGrowthLevel(WorldAccess blockAccess, BlockPos pos) {
        BlockState state = blockAccess.getBlockState(pos.down());
        Block blockBelow = state.getBlock();

        if (blockBelow != null && state != blockBelow.getDefaultState()) {
            return blockBelow.btwr$getWeedsGrowthLevel(blockAccess, pos.down());
        }

        return 0;
    }

    @Override
    public void btwr$removeWeeds(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState( pos.down()).getBlock();
        if (blockBelow != null) {
           blockBelow.btwr$removeWeeds(world, pos.down());
        }
    }
}
