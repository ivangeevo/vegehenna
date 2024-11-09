package org.ivangeevo.vegehenna;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.item.ModItems;

public class VegehennaItemGroup
{

    public static final ItemGroup GROUP_BTWR = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(VegehennaMod.MOD_ID, "group_vegehenna"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.group_vegehenna"))
                    .icon(() -> new ItemStack(ModItems.GROUP_VEGEHENNA))
                    .entries((displayContext, entries) ->
                    {
                        /** ITEMS **/

                        // Uncategorized
                        entries.add(ModItems.STRAW);
                        entries.add(ModItems.FLOUR);
                        entries.add(ModItems.BREAD_DOUGH);
                        entries.add(ModItems.CARROT_SEEDS);
                        entries.add(ModItems.COCOA_POWDER);
                        entries.add(ModItems.PASTRY_UNCOOKED_COOKIES);
                        entries.add(ModItems.PASTRY_UNCOOKED_CAKE);
                        entries.add(ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE);
                        entries.add(ModItems.SUGAR_CANE_ROOTS);


                    }).build());

    public static void registerItemGroups() {
        /**
        // Example of adding to existing Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
         {

            entries.add(BTWR_Items.CREEPER_OYSTERS);

        });
         **/
    }
}
