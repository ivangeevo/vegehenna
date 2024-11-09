package org.ivangeevo.vegehenna.item;

import net.minecraft.block.Blocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.ModBlocks;

public class ModItems
{

    public static final Item GROUP_VEGEHENNA = registerItem( "group_vegehenna", asSimpleItem());

    public static final Item STRAW = registerItem("straw", asSimpleItem());
    public static final Item FLOUR = registerItem("flour", asSimpleItem());
    public static final Item BREAD_DOUGH = registerItem("bread_dough", asSimpleItem());
    public static final Item COCOA_POWDER = registerItem("cocoa_powder", asSimpleItem());
    public static final Item PASTRY_UNCOOKED_COOKIES = registerItem("pastry_uncooked_cookies", asSimpleItem());
    public static final Item PASTRY_UNCOOKED_CAKE = registerItem("pastry_uncooked_cake", asSimpleItem());
    public static final Item PASTRY_UNCOOKED_PUMPKIN_PIE = registerItem("pastry_uncooked_pumpkin_pie", asSimpleItem());

    // BlockItems
    public static final Item SUGAR_CANE_ROOTS = registerItem("sugar_cane_roots", new AliasedBlockItem(ModBlocks.SUGAR_CANE_ROOTS, new Item.Settings()));
    public static final Item CARROT_SEEDS = registerItem("carrot_seeds", new AliasedBlockItem(Blocks.CARROTS, new Item.Settings()));



    private static Item asSimpleItem() { return new Item( new Item.Settings() ); }

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, Identifier.of(VegehennaMod.MOD_ID,name), item);
    }
    public static void registerModItems()
    {
        VegehennaMod.LOGGER.info("Registering Mod Items for " + VegehennaMod.MOD_ID);
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(BTWR_Items::addItemsToIngredientItemGroup);
    }
}
