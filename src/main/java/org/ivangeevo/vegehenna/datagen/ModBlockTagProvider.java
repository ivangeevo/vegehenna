package org.ivangeevo.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {


    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        addToModTags();
    }

    private void addToModTags()
    {
        getOrCreateTagBuilder(ModTags.Blocks.FERTILIZED_FARMLAND_BLOCKS)
                .add(Blocks.FARMLAND)
                .add(ModBlocks.FARMLAND_FERTILIZED);
    }
}
