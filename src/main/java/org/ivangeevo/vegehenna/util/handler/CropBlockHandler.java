package org.ivangeevo.vegehenna.util.handler;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.shared_library.tag.BTWRConventionalTags;
import org.ivangeevo.vegehenna.block.interfaces.CropBlockAdded;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;


public class CropBlockHandler {

    final static CropBlockHandler INSTANCE = new CropBlockHandler();
    private CropBlockHandler() {

    }
    public static CropBlockHandler getInstance() {
        return INSTANCE;
    }

    public void handleCropGrowth(World world, BlockPos pos, BlockState state, Random rand, Block cropBlock) {

        int timeOfDay = (int)(world.getTime() % 24000L);

        if (timeOfDay > 14000 && timeOfDay < 22000) {
            // night
            // reset grown flag at night
            // or if the player sleeps, or time gets skipped -> that's handled by the HasCropGrownHandler
            if (state.get(DailyGrowthCrop.HAS_GROWN_TODAY)) {
                setHasGrownToday(world, pos, false);
            }
        } else
            if (!state.get(DailyGrowthCrop.HAS_GROWN_TODAY) /**&& getWeedsGrowthLevel(world, pos) == 0**/ && canGrowAtCurrentLightLevel(world, pos, cropBlock)) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if (this.hasViableSoilBelow(world, pos)) {
                float growthChance = ((CropBlockAdded)cropBlock).vegehenna$getBaseGrowthChance();

                if (blockBelow.btwr$getIsFertilizedForPlantGrowth(world, pos.down())) {
                    growthChance *= 2F;
                }

                if (rand.nextFloat() <= growthChance) {
                    incrementGrowthLevel(world, pos, state, cropBlock);
                    updateFlagForGrownToday(world, pos, cropBlock);
                }
            }
        }
    }

    private boolean hasViableSoilBelow(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        boolean isHydratedSoil = blockBelow.btwr$isBlockHydratedForPlantGrowthOn(world, pos.down());
        boolean isAlwaysHydratedSoil = blockBelow.getDefaultState().isIn(BTWRConventionalTags.Blocks.ALWAYS_FERTILE_SOIL);
        return isHydratedSoil || isAlwaysHydratedSoil;
    }

    protected static boolean canGrowAtCurrentLightLevel(World world, BlockPos pos, Block cropBlock) {
        Block bwtLightBlock = Registries.BLOCK.get(Identifier.of("bwt", "light_block"));
        BlockState lightBlockState = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtLightBlock.getDefaultState()
                : Blocks.REDSTONE_LAMP.getDefaultState();
        CropBlockAdded added = (CropBlockAdded)cropBlock;

        if (added.vegehenna$requiresNaturalLight()) {
            return world.getLightLevel(pos) > added.vegehenna$getLightLevelForGrowth() ||
                    isLitLightBlock(world, pos.up(), lightBlockState) ||
                    isLitLightBlock(world, pos.up(2), lightBlockState);
        }
        else {
            return world.getLightLevel(pos) >= added.vegehenna$getLightLevelForGrowth();
        }
    }

        private static boolean isLitLightBlock(World world, BlockPos pos, BlockState lightBlockState) {
        return world.getBlockState(pos).equals(lightBlockState.with(Properties.LIT, true));
    }

    protected static void updateFlagForGrownToday(World world, BlockPos pos, Block cropBlock) {
        // fertilized crops can grow twice in a day
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        if ( blockBelow != null ) {
            if (!blockBelow.btwr$getIsFertilizedForPlantGrowth(world, pos.down()) || ((CropBlock)cropBlock).getAge(world.getBlockState(pos)) % 2 == 0 ) {
                setHasGrownToday(world, pos, true);
            }
        }
    }

    // Sets whether the block has grown today
    public static void setHasGrownToday(World world, BlockPos pos, boolean hasGrown) {
        BlockState currentState = world.getBlockState(pos);
        //TODO: confirm if the blockstate flag below is actually the correct one for what the comment below says it does.
        // This one "2" is Block.NOTIFY_LISTENERS, which is sus :D

        // No notify flag is equivalent to passing '2' for flags to suppress visual updates and block updates
        world.setBlockState(pos, currentState.with(DailyGrowthCrop.HAS_GROWN_TODAY, hasGrown), 2);
    }

    private static int getWeedsGrowthLevel(WorldAccess world, BlockPos pos) {
        // Implement logic for weeds growth level
        return 0;  // Default example, adapt to your logic
    }

    // Method to increment growth level for the crop (Block)
    public static void incrementGrowthLevel(World world, BlockPos pos, BlockState state, Block cropBlock) {
        if (cropBlock instanceof DailyGrowthCrop dailyGrowthCrop) {
            dailyGrowthCrop.vegehenna$incrementGrowthLevel(world, pos, state);
        }
    }

}
