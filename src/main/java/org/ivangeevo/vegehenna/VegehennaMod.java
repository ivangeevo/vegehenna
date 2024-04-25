package org.ivangeevo.vegehenna;

import net.fabricmc.api.ModInitializer;
import org.ivangeevo.vegehenna.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VegehennaMod implements ModInitializer
{
    public static final String MOD_ID = "vegehenna";
    public static final Logger LOGGER = LoggerFactory.getLogger("vegehenna");

    //public BTWRSettings settings;
    private static VegehennaMod instance;
    public static VegehennaMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize()
    {
        ModItems.registerModItems();
        VegehennaItemGroup.registerItemGroups();
    }
}
