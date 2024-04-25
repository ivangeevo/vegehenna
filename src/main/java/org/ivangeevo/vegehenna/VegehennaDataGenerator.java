package org.ivangeevo.vegehenna;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.vegehenna.datagen.ModBlockTagProvider;
import org.ivangeevo.vegehenna.datagen.ModItemTagProvider;
import org.ivangeevo.vegehenna.datagen.ModModelGenerator;
import org.ivangeevo.vegehenna.datagen.ModRecipeProvider;

public class VegehennaDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModModelGenerator::new);

    }

}
