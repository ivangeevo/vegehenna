package org.ivangeevo.vegehenna.event;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.ivangeevo.vegehenna.mixin.ItemEntryAccessor;
import org.ivangeevo.vegehenna.mixin.LootPoolBuilderAccessor;

import java.util.ArrayList;
import java.util.List;

public abstract class ModLootTableEvents
{

    // Register loot table changes
    public static void initialize()
    {
        // didn't work last time I tried
        //replaceSpecificItem(Blocks.SHORT_GRASS.getLootTableKey(), Items.WHEAT_SEEDS, Items.AIR);
        
        // tall grass is modified manually via this mod's datapack for now
        //replaceSpecificItem(Blocks.TALL_GRASS.getLootTableKey(), Items.WHEAT_SEEDS, Items.AIR);

    }

    private static void replaceSpecificItem(RegistryKey<LootTable> registryKey, Item target, Item toReplace)
    {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Check if the key is for target's loot table
            if (registryKey != key) return;

            tableBuilder.modifyPools(builder -> {
                List<LootPoolEntry> l = new ArrayList<>(((LootPoolBuilderAccessor) builder).getEntries().build());
                l.replaceAll(entry -> {
                    if (!(entry instanceof ItemEntry itemEntry))
                        return entry;
                    if (((ItemEntryAccessor) itemEntry).getItem().value() != target)
                        return entry;
                    ((ItemEntryAccessor) entry).setItem(Registries.ITEM.getEntry(toReplace));
                    return entry;
                });

                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(l));
            });
        });
    }

}
