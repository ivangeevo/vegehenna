package org.ivangeevo.vegehenna.datagen;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.ivangeevo.vegehenna.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {


    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        addToVanillaTags();
        addToModTags();
        addToConventionalTags();

    }

    private void addToVanillaTags()
    {
        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.DIRT);

        getOrCreateTagBuilder(BlockTags.CROPS)
                .add(ModBlocks.CARROT_FLOWERING);
    }

    private void addToModTags()
    {

        getOrCreateTagBuilder(ModTags.Blocks.REEDS_CAN_PLANT_ON)
                .forceAddTag(BlockTags.DIRT)
                .forceAddTag(BlockTags.SAND)
                .addOptional(RecipeProviderUtils.ID.ofBWT("grass_planter"))
                .addOptional(RecipeProviderUtils.ID.ofBWT("soil_planter"));
    }

    private void addToConventionalTags()
    {
        getOrCreateTagBuilder(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS)
                .add(ModBlocks.FARMLAND_FERTILIZED);
    }


}
