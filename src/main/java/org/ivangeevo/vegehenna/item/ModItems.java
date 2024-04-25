package org.ivangeevo.vegehenna.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;

public class ModItems
{

    public static final Item GROUP_VEGEHENNA = registerItem( "group_vegehenna", new Item(new FabricItemSettings()));

    public static final Item STRAW = registerItem("straw",new Item(new FabricItemSettings()));

    public static final Item FLOUR = registerItem("flour",new Item(new FabricItemSettings()));
    public static final Item BREAD_DOUGH = registerItem("bread_dough",new Item(new FabricItemSettings()));
    public static final Item CARROT_COOKED = registerItem("carrot_cooked",new Item(new FabricItemSettings()));
    public static final Item CARROT_SEEDS = registerItem("carrot_seeds",new Item(new FabricItemSettings()));
    public static final Item COCOA_POWDER = registerItem("cocoa_powder",new Item(new FabricItemSettings()));

    public static final Item PASTRY_UNCOOKED_COOKIES = registerItem("pastry_uncooked_cookies",new Item(new FabricItemSettings()));
    public static final Item PASTRY_UNCOOKED_CAKE = registerItem("pastry_uncooked_cake",new Item(new FabricItemSettings()));
    public static final Item PASTRY_UNCOOKED_PUMPKIN_PIE = registerItem("pastry_uncooked_pumpkin_pie",new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(VegehennaMod.MOD_ID,name), item);
    }
    public static void registerModItems()
    {
        VegehennaMod.LOGGER.info("Registering Mod Items for " + VegehennaMod.MOD_ID);
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(BTWR_Items::addItemsToIngredientItemGroup);
    }
}
