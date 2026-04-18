package org.btwr.vegehenna;

import net.fabricmc.api.ModInitializer;
import org.btwr.vegehenna.block.ModBlocks;
import org.btwr.vegehenna.config.VGModConfig;
import org.btwr.vegehenna.entity.block.ModBlockEntities;
import org.btwr.vegehenna.event.ModEvents;
import org.btwr.vegehenna.item.ModItems;
import org.btwr.vegehenna.sound.ModSoundEvents;
import org.btwr.vegehenna.util.handler.GourdExplodeBehavior;
import org.btwr.vegehenna.util.handler.HasCropGrownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VegehennaMod implements ModInitializer {

    public static final String MOD_ID = "vegehenna";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlockEntities.registerBlockEntities();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        VegehennaItemGroup.registerItemGroups();
        ModSoundEvents.register();
        ModEvents.register();
        VGModConfig.register();

        // Resets growth flag for daily growth crops when night is skipped forcefully
        HasCropGrownHandler.register();

        GourdExplodeBehavior.register();
    }

}