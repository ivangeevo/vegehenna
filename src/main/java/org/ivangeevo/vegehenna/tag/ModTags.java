package org.ivangeevo.vegehenna.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;

public class ModTags
{

    public static class Items
    {

        /** These tags don't generate using datagen.
         *  Instead, the items should be added manually and
         *  provide for other side mods.

         /** For example the CHOCOLATE_ITEMS Tag provides itself
         * to all other BTWR sidemods to add their items.
         * This can't be done with datagen as not all items are present in this project. **/
        public static final TagKey<Item> CHOCOLATE_ITEMS = createTag("chocolate_items");
        public static final TagKey<Item> RAW_EGGS = createTag("raw_eggs");

        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(VegehennaMod.MOD_ID, name));
        }
    }

    public static class Blocks
    {
        public static final TagKey<Block> FERTILIZED_FARMLAND_BLOCKS =  createTag("fertilized_farmland_blocks");


        private static TagKey<Block> createTag (String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(VegehennaMod.MOD_ID, name));
        }
    }
}
