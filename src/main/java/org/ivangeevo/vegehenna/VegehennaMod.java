package org.ivangeevo.vegehenna;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.entity.block.ModBlockEntities;
import org.ivangeevo.vegehenna.item.ModItems;
import org.ivangeevo.vegehenna.model.WeedsBlockModel;
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

        // Resets growth flag for daily growth crops when night is skipped forcefully
        HasCropGrownHandler.register();
    }

    static class WeedsModelLoadingPlugin implements ModelLoadingPlugin
    {
        final static Identifier WEEDS_ID = RecipeProviderUtils.ID.ofVG("weeds");
        public static final ModelIdentifier WEEDS_MODEL_0 = new ModelIdentifier(WEEDS_ID, "0");
        public static final ModelIdentifier WEEDS_MODEL_1 = new ModelIdentifier(WEEDS_ID, "1");
        public static final ModelIdentifier WEEDS_MODEL_2 = new ModelIdentifier(WEEDS_ID, "2");
        public static final ModelIdentifier WEEDS_MODEL_3 = new ModelIdentifier(WEEDS_ID, "3");

        @Override
        public void onInitializeModelLoader(Context pluginContext) {
            pluginContext.modifyModelOnLoad().register((original, context) -> {
                final ModelIdentifier id = context.topLevelId();
                if (id != null && id.equals(WEEDS_MODEL_0)) {
                    return new WeedsBlockModel();
                } else {
                    return original;
                }
            });
        }
    }
}
