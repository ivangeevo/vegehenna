package org.ivangeevo.vegehenna;

import net.fabricmc.api.ModInitializer;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.entity.block.ModBlockEntities;
import org.ivangeevo.vegehenna.item.ModItems;
import org.ivangeevo.vegehenna.util.GourdBlockHandler;
import org.ivangeevo.vegehenna.util.HasCropGrownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VegehennaMod implements ModInitializer {

    public static final String MOD_ID = "vegehenna";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //public BTWRSettings settings;
    private static VegehennaMod instance;

    public static VegehennaMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        ModBlockEntities.registerBlockEntities();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        VegehennaItemGroup.registerItemGroups();
        //ModLootTableEvents.initialize();

        // Vanilla gourds fall like falling blocks
        GourdBlockHandler.registerFallingBehavior();

        // Resets growth flag for daily growth crops when night is skipped forcefully
        HasCropGrownHandler.register();
    }

}
