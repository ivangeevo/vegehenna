package org.ivangeevo.vegehenna;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.vegehenna.item.ModItems;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
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
