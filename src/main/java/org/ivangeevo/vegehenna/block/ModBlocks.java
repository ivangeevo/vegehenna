package org.ivangeevo.vegehenna.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.blocks.FertilizedFarmlandBlock;

public class ModBlocks
{

    public static final Block FARMLAND_FERTILIZED = registerBlock("farmland_fertilized",
            new FertilizedFarmlandBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .ticksRandomly()
                    .strength(0.6f)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .blockVision(Blocks::always)
                    .suffocates(Blocks::always)));


    private static Block registerBlock(String name, Block block)
    {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(VegehennaMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block)
    {
        return Registry.register(Registries.ITEM, new Identifier(VegehennaMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks()
    {
        VegehennaMod.LOGGER.debug("Registering ModBlocks for " + VegehennaMod.MOD_ID);
    }


}
