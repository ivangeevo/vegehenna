package org.ivangeevo.vegehenna;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.entity.block.ModBlockEntities;
import org.ivangeevo.vegehenna.item.ModItems;
import org.ivangeevo.vegehenna.model.WeedsBlockModel;
import org.ivangeevo.vegehenna.util.CropGrowthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VegehennaMod implements ModInitializer {

    public static final String MOD_ID = "vegehenna";
    public static final Logger LOGGER = LoggerFactory.getLogger("vegehenna");

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

        //CropGrowthHandler.register();

        //ModelLoadingPlugin.register(new WeedsModelLoadingPlugin());
    }

    static class WeedsModelLoadingPlugin implements ModelLoadingPlugin
    {
        public static final ModelIdentifier WEEDS_MODEL = new ModelIdentifier(Identifier.of("vegehenna", "weeds"), "");

        @Override
        public void onInitializeModelLoader(Context pluginContext) {
            pluginContext.modifyModelOnLoad().register((original, context) -> {
                final ModelIdentifier id = context.topLevelId();
                if (id != null && id.equals(WEEDS_MODEL)) {
                    return new WeedsBlockModel();
                } else {
                    return original;
                }
            });
        }
    }
}
