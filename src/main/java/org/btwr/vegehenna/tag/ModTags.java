package org.btwr.vegehenna.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.btwr.vegehenna.VegehennaMod;

public class ModTags {

    public static class Items {

        /** These tags don't generate using datagen.
         *  Instead, the items should be added manually and
         *  provide for other side mods.

         /** For example the CHOCOLATE_ITEMS Tag provides itself
         * to all other BTWR sidemods to add their items.
         * This can't be done with datagen as not all items are present in this project. **/
        public static final TagKey<Item> CHOCOLATE_ITEMS = createTag("chocolate_items");
        public static final TagKey<Item> RAW_EGGS = createTag("raw_eggs");

        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(VegehennaMod.MOD_ID, name));
        }
    }

    public static class Blocks {

        /** Common block tag for blocks that have a HAS_GROWN_TODAY blockstate property attached. **/
        public static final TagKey<Block> DAILY_GROWTH_CROPS = createTag("daily_growth_crops");


        public static final TagKey<Block> REEDS_CAN_PLANT_ON =  createTag("reeds_can_plant_on");

        /** Common block tag for gourd blocks. Normally used to mark them for falling block modification behavior. **/
        public static final TagKey<Block> GOURD_BLOCKS = createTag("gourd_blocks");

        /** Common block tag for blocks that need support under them to stay in place. This is mostly used for falling blocks **/
        public static final TagKey<Block> NEEDS_SUPPORT_BLOCK = createTag("needs_support_block");

        private static TagKey<Block> createTag (String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(VegehennaMod.MOD_ID, name));
        }
    }

}